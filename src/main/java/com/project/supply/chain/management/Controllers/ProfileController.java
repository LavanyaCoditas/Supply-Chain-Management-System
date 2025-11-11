package com.project.supply.chain.management.Controllers;

import com.project.supply.chain.management.ServiceInterfaces.UserService;
import com.project.supply.chain.management.dto.ApiResponse;
import com.project.supply.chain.management.dto.FactoryProductStockResponseDto;
import com.project.supply.chain.management.dto.ImageResponseDto;
import com.project.supply.chain.management.dto.ProfileResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profile/users")
public class ProfileController {




}
