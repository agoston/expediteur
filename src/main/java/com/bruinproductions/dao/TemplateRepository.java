package com.bruinproductions.dao;

import com.bruinproductions.bean.TemplateBean;
import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<TemplateBean, String> {
}
