package com.isycat.servlet;

import com.isycat.servlet.json.JsonRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;

public abstract class ImageActivity<RequestType extends JsonRequest>
        extends ServletActivity<RequestType, RenderedImage> {

    private final String format;

    public ImageActivity(final String format) {
        this.format = format;
    }

    @Override
    public final Object getResponse(final String requestId,
                                    final RequestType request,
                                    final HttpServletRequest servletRequest,
                                    final HttpServletResponse servletResponse) throws IOException {
        servletResponse.setContentType("image/" + format);
        servletResponse.setStatus(200);
        return this.handle(request);
    }

    @Override
    protected void writeResponse(final Object response,
                                 final HttpServletResponse servletResponse) throws IOException {
        if (response instanceof RenderedImage) {
            ImageIO.write((RenderedImage) response, "jpeg", servletResponse.getOutputStream());
        } else {
            servletResponse.getOutputStream().println(response.toString());
        }
    }
}
