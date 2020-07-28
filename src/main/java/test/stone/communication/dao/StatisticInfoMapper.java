package test.stone.communication.dao;

import test.stone.communication.entity.StatisticInfo;

import java.util.List;

public interface StatisticInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StatisticInfo record);

    int insertSelective(StatisticInfo record);

    StatisticInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StatisticInfo record);

    int updateByPrimaryKey(StatisticInfo record);

    List<StatisticInfo> getList();
}