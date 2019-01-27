package com.isycat.burrow.operation;

import com.isycat.burrow.json.JsonRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;

public abstract class ImageOperation<RequestType extends JsonRequest>
        extends AbstractServletOperationHandler<RequestType, RenderedImage> {

    private final String format;

    public ImageOperation(final String format) {
        this.format = format;
    }

    @Override
    public final RenderedImage getResponse(final RequestType request,
                                           final HttpServletRequest servletRequest,
                                           final HttpServletResponse servletResponse) throws Exception {
        servletResponse.setContentType("image/" + format);
        servletResponse.setStatus(200);
        return this.handle(request);
    }

    @Override
    protected void writeResponse(final RenderedImage response,
                                 final HttpServletResponse servletResponse) throws Exception {
        ImageIO.write(response, "jpeg", servletResponse.getOutputStream());
    }
}
