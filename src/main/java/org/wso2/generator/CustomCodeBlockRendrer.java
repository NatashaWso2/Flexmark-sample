package org.wso2.generator;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ext.attributes.AttributesExtension;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.xwiki.macros.Macro;
import com.vladsch.flexmark.ext.xwiki.macros.MacroAttribute;
import com.vladsch.flexmark.ext.xwiki.macros.MacroBlock;
import com.vladsch.flexmark.ext.xwiki.macros.MacroClose;
import com.vladsch.flexmark.ext.xwiki.macros.MacroExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom block class which renders the code block generated
 */
class CustomCodeBlockRendrer implements NodeRenderer {
    final MutableDataSet OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    AttributesExtension.create(), YamlFrontMatterExtension.create(),
                    JekyllTagExtension.create(), MacroExtension.create(), CustomExtension.create()
            ))
            .set(MacroExtension.ENABLE_RENDERING, true);
    final Parser parser = Parser.builder(OPTIONS).build();
    final HtmlRenderer renderer = HtmlRenderer.builder(OPTIONS).build();

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
        set.add(new NodeRenderingHandler<Macro>(Macro.class, new CustomNodeRenderer<Macro>() {
            @Override
            public void render(Macro node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<MacroAttribute>(MacroAttribute.class, new CustomNodeRenderer<MacroAttribute>() {
            @Override
            public void render(MacroAttribute node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<MacroClose>(MacroClose.class, new CustomNodeRenderer<MacroClose>() {
            @Override
            public void render(MacroClose node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<MacroBlock>(MacroBlock.class, new CustomNodeRenderer<MacroBlock>() {
            @Override
            public void render(MacroBlock node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<TableBlock>(TableBlock.class, new CustomNodeRenderer<TableBlock>() {
            @Override
            public void render(TableBlock node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));
        set.add(new NodeRenderingHandler<Image>(Image.class, new CustomNodeRenderer<Image>() {
            @Override
            public void render(Image node, NodeRendererContext context, HtmlWriter html) {
                CustomCodeBlockRendrer.this.render(node, context, html);
            }
        }));

        return set;
    }

    /**
     * Renders images
     *
     * @param node
     * @param context
     * @param htmlWriter
     */
    private void render(Image node, NodeRendererContext context, HtmlWriter htmlWriter) {
        htmlWriter.line();
        htmlWriter.attr("class", "image-block");
        htmlWriter.attr("src", node.getPageRef());
        htmlWriter.attr("alt", node.getText());
        htmlWriter.attr("title", node.getTitle());
        htmlWriter.withAttr().tag("img");
        htmlWriter.closeTag("img");
        htmlWriter.line();
    }

    /**
     * Renders a table
     *
     * @param node
     * @param context
     * @param htmlWriter
     */
    private void render(TableBlock node, NodeRendererContext context, HtmlWriter htmlWriter) {
        htmlWriter.attr("class", "table-style");
    }

    /**
     * Renders the macro close node
     *
     * @param node
     * @param context
     * @param htmlWriter
     */
    private void render(MacroClose node, NodeRendererContext context, HtmlWriter htmlWriter) {
    }

    /**
     * Renders the macro block
     *
     * @param node
     * @param context
     * @param htmlWriter
     */
    private void render(MacroBlock node, NodeRendererContext context, HtmlWriter htmlWriter) {
        htmlWriter.line();
        htmlWriter.attr("class", "marco-block marco-block-" + node.getMacroNode().getName());
        htmlWriter.withAttr().tag("div");

        if (node.getAttributes().size() > 0) {
            htmlWriter.line();
            htmlWriter.attr("class", "marco-title");
            htmlWriter.withAttr().tag("span");
            htmlWriter.text(node.getAttributes().get("title"));
            htmlWriter.closeTag("span");
        }


        context.renderChildren(node);
        htmlWriter.closeTag("div");
        htmlWriter.line();
    }

    /**
     * Renders the macro attribute node
     *
     * @param node
     * @param context
     * @param htmlWriter
     */
    private void render(MacroAttribute node, NodeRendererContext context, HtmlWriter htmlWriter) {
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
        /*String comment_format;
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
        htmlWriter.closeTag("div");*/

        htmlWriter.withAttr().tag("div");
        htmlWriter.tag("pre").openPre();
        htmlWriter.attr("class", "language-" + fencedCodeBlock.getInfo());
        htmlWriter.withAttr().tag("code");
        htmlWriter.text(fencedCodeBlock.getFirstChild().getChars().toString());
        htmlWriter.closeTag("code");
        htmlWriter.closeTag("pre").closePre();
        htmlWriter.closeTag("div");

        htmlWriter.line();
    }

    /**
     * Renders the macro node
     *
     * @param node
     * @param nodeRendererContext
     * @param htmlWriter
     */
    public void render(Macro node, NodeRendererContext nodeRendererContext, HtmlWriter htmlWriter) {
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
