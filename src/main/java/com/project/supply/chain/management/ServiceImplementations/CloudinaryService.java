//package com.project.supply.chain.management.ServiceImplementations;
//
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//public class CloudinaryService {
//
//    @Autowired
//    private Cloudinary cloudinary;
//
//    public String uploadImage(MultipartFile file) throws IOException {
//        // Convert MultipartFile to File object
//        java.io.File convertedFile = convertToFile(file);
//
//        // Upload the file to Cloudinary
//        Map<String, String> uploadResult = cloudinary.uploader().upload(convertedFile, ObjectUtils.emptyMap());
//        return uploadResult.get("url");  // This returns the image URL
//    }
//
//    private java.io.File convertToFile(MultipartFile file) throws IOException {
//        java.io.File convertedFile = new java.io.File(file.getOriginalFilename());
//        file.transferTo(convertedFile);
//        return convertedFile;
//    }
//}
package com.project.supply.chain.management.ServiceImplementations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));

        return uploadResult.get("secure_url").toString();
    }
}
