package com.kanna.imageMerge.repo;

import com.kanna.imageMerge.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepo extends JpaRepository<Image,Long> {

}
