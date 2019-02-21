package br.com.cssis.foundation.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;

import br.com.cssis.foundation.BasicException;

public interface DBTransporter extends Serializable {
    void populate(ResultSet row) throws BasicException;
    void setFieldList(List<String> fieldList);
}
