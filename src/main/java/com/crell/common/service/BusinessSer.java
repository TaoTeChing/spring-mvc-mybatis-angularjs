package com.crell.common.service;

import com.crell.common.model.Business;
import com.crell.core.dto.Page;

import java.util.Map;

/**
 *
 */
public interface BusinessSer {

    Boolean add(Business business);

    Boolean update(Business business);

    Boolean del(String businessId);

    Page getBusinessList(Map<String, Object> body, Page page);

    Business getBusinessById(String businessId);
}
