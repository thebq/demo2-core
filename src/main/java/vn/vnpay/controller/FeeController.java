package vn.vnpay.controller;

import vn.vnpay.dto.CreateFeeRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/fee")
public class FeeController {
    @Path("/create")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Object createFee(CreateFeeRequest createFeeRequest) {
        return null;
    }

}
