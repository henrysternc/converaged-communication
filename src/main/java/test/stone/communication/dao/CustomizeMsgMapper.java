package test.stone.communication.dao;

import test.stone.communication.entity.CustomizeMsg;

public interface CustomizeMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomizeMsg record);

    int insertSelective(CustomizeMsg record);

    CustomizeMsg selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomizeMsg record);

    int updateByPrimaryKey(CustomizeMsg record);
}