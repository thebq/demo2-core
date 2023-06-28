package vn.vnpay.controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/v1/fee")
public class FeeController {
    @Path("/create")
    @POST
    public Object createFee() {
        return null;
    }

}
