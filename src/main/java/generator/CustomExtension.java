package generator;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataHolder;

/**
 * Custom extension to customize rendered code blocks
 */
public class CustomExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private CustomExtension() {
    }

    /**
     * Create the extension
     *
     * @return CustomExtension object
     */
    public static Extension create() {
        return new CustomExtension();
    }

    /**
     * Extend the Parser
     *
     * @param parserBuilder
     */
    @Override
    public void extend(Parser.Builder parserBuilder) {
    }

    /**
     * Render set of options passed when parsing
     *
     * @param options
     */
    @Override
    public void rendererOptions(final MutableDataHolder options) {

    }

    /**
     * Parse options when parsing
     *
     * @param options
     */
    @Override
    public void parserOptions(final MutableDataHolder options) {

    }

    /**
     * Render the HTML from the Custom Extension
     *
     * @param rendererBuilder HtmlRenderer object
     * @param rendererType    type of the renderer type
     */
    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        if (rendererType.equals("HTML")) {
            rendererBuilder.nodeRendererFactory(new CustomCodeBlockRendrer.Factory());
        } else if (rendererType.equals("JIRA") || rendererType.equals("YOUTRACK")) {
        }
    }
}