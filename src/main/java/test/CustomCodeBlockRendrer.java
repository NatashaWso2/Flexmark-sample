package test;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;

import java.util.HashSet;
import java.util.Set;

class CustomCodeBlockRendrer implements NodeRenderer {

    CustomCodeBlockRendrer(DataHolder dataHolder) {
    }

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

    public void render(FencedCodeBlock fencedCodeBlock, NodeRendererContext nodeRendererContext, HtmlWriter htmlWriter) {
        htmlWriter.line();
        // Logic to separate the comments and the bal code should be written here
        htmlWriter.tag("div");
        htmlWriter.tag("pre").openPre();
        htmlWriter.attr("class", "language-ballerina");
        htmlWriter.withAttr().tag("code");
        htmlWriter.text(fencedCodeBlock.getFirstChild().getChars());
        //htmlWriter.text("helllo I was replaced by the code block");
        htmlWriter.closeTag("code");
        htmlWriter.closeTag("pre").closePre();
        htmlWriter.closeTag("div");
        htmlWriter.line();
    }

    public static class Factory implements NodeRendererFactory {
        @Override
        public NodeRenderer create(final DataHolder options) {
            return new CustomCodeBlockRendrer(options);
        }
    }
}
