package test.stone.communication.dao;

import test.stone.communication.entity.DelayDetail;

public interface DelayDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DelayDetail record);

    int insertSelective(DelayDetail record);

    DelayDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DelayDetail record);

    int updateByPrimaryKey(DelayDetail record);
}