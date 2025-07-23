package com.jsonplaceholder.api.service.impl;

import com.jsonplaceholder.api.dto.UserDto;
import com.jsonplaceholder.api.dto.AddressDto;
import com.jsonplaceholder.api.dto.GeoDto;
import com.jsonplaceholder.api.dto.CompanyDto;
import com.jsonplaceholder.api.model.User;
import com.jsonplaceholder.api.model.Address;
import com.jsonplaceholder.api.model.Geo;
import com.jsonplaceholder.api.model.Company;
import com.jsonplaceholder.api.repository.UserRepository;
import com.jsonplaceholder.api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = mapToEntity(userDto);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        User user = mapToEntity(userDto);
        user.setId(id);
        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setWebsite(user.getWebsite());
        
        if (user.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setSuite(user.getAddress().getSuite());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setZipcode(user.getAddress().getZipcode());
            
            if (user.getAddress().getGeo() != null) {
                GeoDto geoDto = new GeoDto();
                geoDto.setLat(user.getAddress().getGeo().getLat());
                geoDto.setLng(user.getAddress().getGeo().getLng());
                addressDto.setGeo(geoDto);
            }
            
            dto.setAddress(addressDto);
        }
        
        if (user.getCompany() != null) {
            CompanyDto companyDto = new CompanyDto();
            companyDto.setName(user.getCompany().getName());
            companyDto.setCatchPhrase(user.getCompany().getCatchPhrase());
            companyDto.setBs(user.getCompany().getBs());
            dto.setCompany(companyDto);
        }
        
        return dto;
    }

    private User mapToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setWebsite(dto.getWebsite());
        
        if (dto.getAddress() != null) {
            Address address = new Address();
            address.setStreet(dto.getAddress().getStreet());
            address.setSuite(dto.getAddress().getSuite());
            address.setCity(dto.getAddress().getCity());
            address.setZipcode(dto.getAddress().getZipcode());
            
            if (dto.getAddress().getGeo() != null) {
                Geo geo = new Geo();
                geo.setLat(dto.getAddress().getGeo().getLat());
                geo.setLng(dto.getAddress().getGeo().getLng());
                address.setGeo(geo);
            }
            
            user.setAddress(address);
        }
        
        if (dto.getCompany() != null) {
            Company company = new Company();
            company.setName(dto.getCompany().getName());
            company.setCatchPhrase(dto.getCompany().getCatchPhrase());
            company.setBs(dto.getCompany().getBs());
            user.setCompany(company);
        }
        
        return user;
    }
} 