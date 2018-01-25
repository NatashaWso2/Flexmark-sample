package test;

import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTag;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    static final MutableDataSet OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    CustomExtension.create(), AttributesExtension.create(), YamlFrontMatterExtension.create(),
                    JekyllTagExtension.create()
            ));
    // .set(HtmlRenderer.UNESCAPE_HTML_ENTITIES, false);
    static String userDir = System.getProperty("user.dir");
    static String generated_file_directory = userDir + "/src/main/resources/generated/";
    static String layout_template_directory = userDir + "/src/main/resources/layouts/";
    static String markdown_file_directory = userDir + "/src/main/resources/markdown/";
    static String included_file_directory = userDir + "/src/main/resources/included/";
    static String default_template = "default";
    static Parser parser = Parser.builder(OPTIONS).build();
    static HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();

    public static void init() throws IOException {
        List<File> fileList = listAllFilesInDirectory(markdown_file_directory);

        for (File file : fileList) {
            String markdown = readMarkdownFile(file.getName());
            Node document = processMarkDownFile(markdown);
            // Get the yaml front matter properties from markdown
            AbstractYamlFrontMatterVisitor visitor = new AbstractYamlFrontMatterVisitor();
            visitor.visit(document);
            Map<String, List<String>> frontMatterList = visitor.getData();

            // Decide the layout from the front matter declared
            StringBuilder layoutHTML;
            if (frontMatterList.get("layout") != null) {
                String layoutTemplate = frontMatterList.get("layout").get(0);
                layoutHTML = new StringBuilder(getLayoutContent(layoutTemplate));
            } else {
                layoutHTML = new StringBuilder(getLayoutContent(default_template));
            }

            // Generate html content from markdown
            String generatedHtmlFromMarkdown = renderer.render(document);

            // Manipulate the layout html
            addIncludedFilesInHTML(layoutHTML);
            generateHtmlFullDocument(layoutHTML, frontMatterList, generatedHtmlFromMarkdown);


            // Get file name for the generated html
            String htmlFileName = file.getName().replace(".md", "");
            writeHtmlToFile(htmlFileName, layoutHTML.toString());
        }

    }

    public static Node processMarkDownFile(String markdown) throws IOException {
        Node document = parser.parse(markdown);

        // see if markdown document has includes
        if (document instanceof Document) {
            Document doc = (Document) document;
            if (doc.contains(JekyllTagExtension.TAG_LIST)) {
                List<JekyllTag> tagList = JekyllTagExtension.TAG_LIST.getFrom(doc);
                Map<String, String> includeHtmlMap = new HashMap<>();

                for (JekyllTag tag : tagList) {
                    String includeFile = tag.getParameters().toString();
                    if (tag.getTag().equals("include") && !includeFile.isEmpty() && !includeHtmlMap.containsKey(includeFile)) {
                        // see if it exists
                        Map<String, String> included = getIncludedFilesWithContent();
                        if (included.containsKey(includeFile)) {
                            // have the file
                            String text = included.get(includeFile);

                            if (includeFile.endsWith(".md")) {
                                Node includeDoc = parser.parse(text);
                                String includeHtml = renderer.render(includeDoc);
                                includeHtmlMap.put(includeFile, includeHtml);

                                if (includeDoc instanceof com.vladsch.flexmark.ast.Document) {
                                    // copy any definition of reference elements from included file to our document
                                    parser.transferReferences(doc, (com.vladsch.flexmark.ast.Document) includeDoc);
                                }
                            } else {
                                includeHtmlMap.put(includeFile, text);
                            }
                        }
                    }

                    if (!includeHtmlMap.isEmpty()) {
                        doc.set(JekyllTagExtension.INCLUDED_HTML, includeHtmlMap);
                    }
                }
            }
        }
        return document;
    }

    public static void writeHtmlToFile(String fileName, String htmlContent) throws IOException {
        String filePath = generated_file_directory + fileName + ".html";
        Files.write(Paths.get(filePath), htmlContent.getBytes());
    }

    public static String getLayoutContent(String layoutTemplateFileName) throws IOException {
        return Files.lines(Paths.get(layout_template_directory + layoutTemplateFileName + ".html")).
                collect(Collectors.joining("\n"));
    }

    public static String readMarkdownFile(String name) throws IOException {
        String markdownContent = Files.lines(Paths.get(markdown_file_directory + name))
                .collect(Collectors.joining("\n"));
        return markdownContent;
    }

    public static List<File> listAllFilesInDirectory(String fileDir) throws IOException {
        List<File> filesInFolder = Files.walk(Paths.get(fileDir))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        return filesInFolder;
    }

    public static Map<String, String> getIncludedFilesWithContent() throws IOException {
        List<File> fileList = listAllFilesInDirectory(included_file_directory);
        Map<String, String> included = new HashMap<>();
        for (File file : fileList) {
            String content = Files.lines(Paths.get(included_file_directory + file.getName()))
                    .collect(Collectors.joining("\n"));
            included.put(file.getName(), content);
        }
        return included;
    }

    public static void generateHtmlFullDocument(StringBuilder layoutContent, Map<String, List<String>> frontMatterList, String generatedHtmlFromMarkdown) {
        Pattern pattern = Pattern.compile("\\{\\{(\\w*)\\}\\}");
        Matcher matchPattern = pattern.matcher(layoutContent);
        while (matchPattern.find()) {
            String matchedWord = matchPattern.group(0);

            // Remove curly braces from word
            matchedWord = matchedWord.replaceAll("\\{", "");
            matchedWord = matchedWord.replaceAll("\\}", "");

            if (matchedWord.equals("content")) {
                layoutContent = layoutContent.replace(matchPattern.start(), matchPattern.end(), generatedHtmlFromMarkdown);
            } else {
                String replacement = frontMatterList.get(matchedWord).get(0);
                layoutContent = layoutContent.replace(matchPattern.start(), matchPattern.end(), replacement);
            }
        }
    }

    public static void addIncludedFilesInHTML(StringBuilder layoutContent) throws IOException {
        Pattern pattern = Pattern.compile("\\{[%](.*?)\\}");
        Matcher matchPattern = pattern.matcher(layoutContent);
        while (matchPattern.find()) {
            String matchedWord = matchPattern.group(0);

            // Remove curly braces from word
            matchedWord = matchedWord.replaceAll("\\{% include", "").trim();
            matchedWord = matchedWord.replaceAll("%\\}", "").trim();

            // Generate HTML content from the matched file name
            String matchedFileContent = readMarkdownFile(matchedWord);
            String generatedHtml = renderer.render(processMarkDownFile(matchedFileContent));
            layoutContent = layoutContent.replace(matchPattern.start(), matchPattern.end(), generatedHtml);

        }
    }

    public static void main(String[] args) throws IOException {
        init();
    }
}