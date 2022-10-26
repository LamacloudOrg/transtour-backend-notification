package com.transtour.backend.notification.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.InputStream;

@Qualifier("VoucherClient")
@FeignClient(name = "SPRING-CLOUD-VOUCHER-API")
public interface IVoucherRepository {

    /*
    @RequestMapping(method = RequestMethod.POST, value = "/v1/voucher/{travelId}")
    String getVoucher(@PathVariable String travelId);
    */

    @RequestMapping(method = RequestMethod.GET, value = "/v1/voucher/downloadPdf/{id}")
    ResponseEntity getVoucher(@PathVariable Long id);

}