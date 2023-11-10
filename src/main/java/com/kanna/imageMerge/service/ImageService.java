package com.kanna.imageMerge.service;

import com.kanna.imageMerge.entity.Image;
import com.kanna.imageMerge.repo.ImageRepo;
import com.kanna.imageMerge.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ImageService {

    private final ImageRepo imageRepo;

    public ImageService(ImageRepo imageRepo) {
        this.imageRepo = imageRepo;
    }

    public String  uploadImage(MultipartFile file) throws IOException {

        imageRepo.save(Image.builder()
                        .imageData(ImageUtil.compressImage(file.getBytes()))
                .build());

            return "file uploaded successfully : " + file.getOriginalFilename();
        }
    public byte[] downloadImage(Long id) {
        Optional<Image> dbImageData = imageRepo.findById(id);
         return ImageUtil.decompressImage(dbImageData.get().getImageData());
    }
    public List<Image> getAll(){
        return imageRepo.findAll();
    }
    public String delete(Long id){
        imageRepo.deleteById(id);
        return "deleted";
    }
    public Optional<Image> oneImage(Long id){
        return imageRepo.findById(id);
    }
    public void saveImagewithIcon(MultipartFile file) throws IOException {
        byte[] orgImageData = file.getBytes();
        byte[] modifiedImageData = addPlayIcon(orgImageData);


        log.info("modified Image data {}",modifiedImageData.length);

       imageRepo.save(Image.builder()
                .imageData(modifiedImageData)
                .build());

    }

    private byte[] addPlayIcon(byte[] orgImageData) throws IOException {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(orgImageData);
        BufferedImage orgImage = ImageIO.read(inputStream);

        InputStream playIconStream = getClass().getResourceAsStream("/images/icons8-play-64.png");
        BufferedImage playIcon = ImageIO.read(playIconStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(orgImage)
                .size(orgImage.getWidth(),orgImage.getHeight())
                .watermark(Positions.BOTTOM_RIGHT, playIcon, 1.0f)
                .outputQuality(1.0)
                .outputFormat("png")
                .toOutputStream(outputStream);

        log.info("output stream  {}",outputStream.size());


       return outputStream.toByteArray();

    }
    public void some(){
        imageRepo.findAll().stream().map(Image::getId).forEach(id->log.info("id {}",id));
    }


}
