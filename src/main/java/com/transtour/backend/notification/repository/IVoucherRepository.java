package com.transtour.backend.notification.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.io.InputStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Qualifier("VoucherClient")
@FeignClient(name = "SPRING-CLOUD-VOUCHER-API")
public interface IVoucherRepository {

    /*
    @RequestMapping(method = RequestMethod.POST, value = "/v1/voucher/{travelId}")
    String getVoucher(@PathVariable String travelId);
    */

    /*
    @RequestMapping(method = GET, value = "/v1/voucher/downloadPdf/{id}")
    ResponseEntity getVoucher(@PathVariable Long id);
    */

    @RequestMapping(
            value = "/v1/voucher/downloadPdf/{id}",
            method = GET)
    ResponseEntity<Multipart> getVoucher(@PathVariable("id") Long id);
}