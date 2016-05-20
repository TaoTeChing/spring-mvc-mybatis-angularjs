package com.crell.common.mapper;

import com.crell.common.model.Dictionary;
import com.crell.core.annotation.DataSource;
import org.springframework.stereotype.Repository;

/**
 * Created by crell on 2016/5/18.
 */
@Repository
public interface DictionaryMapper {

    @DataSource("master")
    Boolean add(Dictionary dictionary);

}
