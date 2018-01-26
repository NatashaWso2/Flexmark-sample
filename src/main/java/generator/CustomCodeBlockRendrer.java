package generator;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Custom block class which renders the code block generated
 */
class CustomCodeBlockRendrer implements NodeRenderer {

    CustomCodeBlockRendrer(DataHolder dataHolder) {
    }

    /**
     * Get node rendering handlers for different kinds of nodes
     *
     * @return cutom node rendering handlers
     */
    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<NodeRenderingHandler<?>>();
        set.add(new NodeRenderingHandler<FencedCodeBlock>(FencedCodeBlock.class, new CustomNodeRenderer<FencedCodeBlock>() {
            @Override
            public void render(FencedCodeBlock fencedCodeBlock, NodeRendererContext nodeRendererContext, HtmlWriter htmlWriter) {
                CustomCodeBlockRendrer.this.render(fencedCodeBlock, nodeRendererContext, htmlWriter);
            }
        }));

        return set;
    }

    /**
     * Renders the code block after customizing it
     *
     * @param fencedCodeBlock
     * @param nodeRendererContext
     * @param htmlWriter
     */
    public void render(FencedCodeBlock fencedCodeBlock, NodeRendererContext nodeRendererContext, HtmlWriter htmlWriter) {
        htmlWriter.line();
        String comment_format;
        // Logic to separate the comments and the bal code should be written here
        if (fencedCodeBlock.getInfo().equals("ballerina")) {
            comment_format = "//";
        } else {
            comment_format = "#";
        }
        //
        List<String> code_vector = new ArrayList<>();
        List<String> comments_vector = new ArrayList<>();
        List<String> content_vector = Arrays.asList(fencedCodeBlock.getFirstChild().getChars().toString().split("\n"));

        for (String line : content_vector) {
            int index = content_vector.indexOf(line);
            // line = line.trim();
            if (line.trim().startsWith(comment_format)) {
                // Trim the comment for whitespaces and remove the starting `//`
                line = line.replace(comment_format, "");
                comments_vector.add(line.trim());
            } else {
                code_vector.add(line);
                if (comments_vector.size() < code_vector.size()) {
                    comments_vector.add("\n");
                }
                if (comments_vector.size() > code_vector.size()) {
                    if (index < content_vector.size() - 1) {
                        if (content_vector.get(index + 1).trim().startsWith(comment_format)
                                || content_vector.get(index + 1).isEmpty()) {
                            IntStream.range(code_vector.size(), comments_vector.size()).forEach(
                                    nbr -> code_vector.add("\n")
                            );
                        }
                    }
                }
            }
        }
        // Make both the comments and code vectors equal in size
        if (comments_vector.size() > code_vector.size()) {
            IntStream.range(code_vector.size(), comments_vector.size()).forEach(
                    nbr -> code_vector.add("\n")
            );
        } else {
            IntStream.range(comments_vector.size(), code_vector.size()).forEach(
                    nbr -> comments_vector.add("\n")
            );
        }
        String comments = String.join("\n", comments_vector);
        String code = String.join("\n", code_vector);

        htmlWriter.attr("name", "blockDiv");
        htmlWriter.withAttr().tag("div");

        // Comment
        htmlWriter.attr("class", "commentDiv");
        htmlWriter.withAttr().tag("div");
        htmlWriter.attr("name", "comment-block");
        htmlWriter.withAttr().tag("span");
        htmlWriter.text(comments);
        htmlWriter.closeTag("span");
        htmlWriter.closeTag("div");

        // Code
        htmlWriter.attr("class", "codeDiv");
        htmlWriter.withAttr().tag("div");
        htmlWriter.tag("pre").openPre();
        htmlWriter.attr("class", "language-" + fencedCodeBlock.getInfo());
        htmlWriter.withAttr().tag("code");
        htmlWriter.text(code);
        htmlWriter.closeTag("code");
        htmlWriter.closeTag("pre").closePre();
        htmlWriter.closeTag("div");

        htmlWriter.closeTag("div");

        htmlWriter.line();
    }

    /**
     * NodeRenderFactory implementation to call the custom node render
     */
    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer create(final DataHolder options) {
            return new CustomCodeBlockRendrer(options);
        }
    }
}
