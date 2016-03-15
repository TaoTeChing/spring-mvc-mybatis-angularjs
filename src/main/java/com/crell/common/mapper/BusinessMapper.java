package com.crell.common.mapper;

import com.crell.common.model.Business;
import com.crell.core.dto.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by FunkySoya on 2015/6/18.
 */
@Repository
public interface BusinessMapper {

    Boolean add(Business business);

    Boolean update(Business business);

    Boolean del(@Param("businessId") String businessId);

    List<Business> getBusinessList(Page page);

    Business getBusinessById(@Param("businessId") String businessId);
}
