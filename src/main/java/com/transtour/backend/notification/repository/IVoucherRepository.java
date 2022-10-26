package com.transtour.backend.notification.repository;

import com.transtour.backend.notification.util.InMemoryMultipartFile;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Qualifier("VoucherClient")
@FeignClient(name = "SPRING-CLOUD-VOUCHER-API", configuration = IVoucherRepository.MultipartSupportConfig.class)
public interface IVoucherRepository {

    @RequestMapping(
            value = "/v1/voucher/downloadPdf/{id}",
            method = GET)
    ResponseEntity<MultipartFile> getVoucher(@PathVariable("id") Long id);

    class MultipartSupportConfig {

        @Autowired
        ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        @Scope("prototype")
        public Encoder multipartFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }

        @Bean
        @Primary
        @Scope("prototype")
        public Decoder decoder() {
            Decoder decoder = (response, type) -> {
                if (type instanceof Class && MultipartFile.class.isAssignableFrom((Class) type)) {
                    Collection<String> contentTypes = response.headers().get("content-type");
                    String contentType = "application/octet-stream";
                    if (contentTypes.size() > 0) {
                        String[] temp = new String[contentTypes.size()];
                        contentTypes.toArray(temp);
                        contentType = temp[0];
                    }


                    byte[] bytes = StreamUtils.copyToByteArray(response.body().asInputStream());
                    InMemoryMultipartFile inMemoryMultipartFile = new InMemoryMultipartFile("file", "", contentType, bytes);
                    return inMemoryMultipartFile;
                }
                return new SpringDecoder(messageConverters).decode(response, type);
            };
            return new ResponseEntityDecoder(decoder);
        }
    }

}