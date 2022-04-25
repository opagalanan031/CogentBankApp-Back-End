package com.learning.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.entity.Role;
import com.learning.entity.User;
import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;
import com.learning.exception.EnumNotFoundException;
import com.learning.exception.IdNotFoundException;
import com.learning.exception.UnauthorizedAccessException;
import com.learning.payload.request.AuthenticationRequest;
import com.learning.payload.request.admin.CreateStaffRequest;
import com.learning.payload.request.admin.SetStaffEnabledRequest;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.admin.GetStaffResponse;
import com.learning.payload.response.customer.CustomerRegisterResponse;
import com.learning.payload.response.staff.StaffResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserServiceImpl userService ;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> signin(@Valid @RequestBody AuthenticationRequest signinRequest) {
	
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						signinRequest.getUsername(), 
						signinRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = jwtUtils.generateToken(authentication);
		// get user data/ principal
	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetailsImpl.getAuthorities()
				.stream().map(e -> e.getAuthority())
				.collect(Collectors.toList());
		// return new token
		boolean isadmin = false;
		for( int i =0 ; i < roles.size() ; i++) {
			if (roles.get(i).equals(ERole.ROLE_ADMIN.name())) {
				isadmin= true;
			}
		}
		if(!isadmin) {
			throw new UnauthorizedAccessException("unauthorized access");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new JwtResponse(jwt, 
						userDetailsImpl.getId(), 
						userDetailsImpl.getUsername(), 
						userDetailsImpl.getFullName(), userDetailsImpl.getStatus(),
						roles));
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/staff")
	public ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffRequest request) {
		
		User staff = new User();
		staff.setUsername(request.getUsername());
		staff.setFullName(request.getFullName());
		staff.setPassword(passwordEncoder.encode(request.getPassword()));
		staff.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		Role staffRole = roleRepository.findByRoleName(ERole.ROLE_STAFF)
				.orElseThrow(() -> 
					new EnumNotFoundException("Staff role not in database."));
		Set<Role> roles = new HashSet<>();
		roles.add(staffRole);
		staff.setRoles(roles);
		User regUser = userService.addUser(staff);
		
		CustomerRegisterResponse registerResponse = new CustomerRegisterResponse();
		registerResponse.setId(regUser.getId());
		registerResponse.setUsername(regUser.getUsername());
		registerResponse.setFullName(regUser.getFullName());
		registerResponse.setPassword(regUser.getPassword());
		
		return ResponseEntity.status(HttpStatus.OK).body(registerResponse);
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/staff")
	public ResponseEntity<?> getAllStaff() {
		List<User> staff = new ArrayList<>();
		staff = userService.findAllByRoleName(ERole.ROLE_STAFF);
		List<GetStaffResponse> staffResponses = new ArrayList<GetStaffResponse>();
		System.out.println(staff.size());
		if (staff.size() > 0) {
			for (User user : staff) {
				GetStaffResponse staffResponse = new GetStaffResponse();
				staffResponse.setStaffId(user.getId());
				staffResponse.setStaffUsername(user.getUsername());
				staffResponse.setStaffName(user.getFullName());
				staffResponse.setStatus(user.getEnabledStatus());
				staffResponses.add(staffResponse);
			}

		}
		return ResponseEntity.status(HttpStatus.OK).body(staffResponses);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/staff")
	public ResponseEntity<?> setStaffEnabled(@Valid @RequestBody SetStaffEnabledRequest request) {
		User staff = (User) userService.getUserById(request.getStaffId()).orElseThrow(
					()-> new IdNotFoundException("Staff status not changed")
				);
		
		System.out.println(request.getStatus());
		//Map<String, String> response = new LinkedHashMap<>();
		//response.put("staffId", " : " + request.getStaffId());
		if(request.getStatus() == EnabledStatus.STATUS_ENABLED) {
			
			staff.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		}	
		else {
			staff.setEnabledStatus(EnabledStatus.STATUS_DISABLED);
		}
		
		userService.updateUser(staff);
		StaffResponse  staffResponse = new StaffResponse();
		staffResponse.setStaffId(staff.getId());
		staffResponse.setStaffUserName(staff.getUsername());
		staffResponse.setStaffName(staff.getFullName());
		staffResponse.setStatus(staff.getEnabledStatus());
		
		return ResponseEntity.status(HttpStatus.OK).body(staffResponse);
	}
	
}
