package vn.vnpay.controller;

import vn.vnpay.dto.CreateFeeRequest;
import vn.vnpay.service.FeeService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/fee")
public class FeeController {
    private FeeService feeService;

    @Path("/create")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Object createFee(CreateFeeRequest createFeeRequest) {
        return feeService.createFee();
    }

    @Path("/create/transaction")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Object createFeeTransaction(){return feeService.createFeeTransaction();}
}
