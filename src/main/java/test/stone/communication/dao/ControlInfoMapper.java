package test.stone.communication.dao;

import test.stone.communication.entity.ControlInfo;

public interface ControlInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ControlInfo record);

    int insertSelective(ControlInfo record);

    ControlInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ControlInfo record);

    int updateByPrimaryKey(ControlInfo record);
}