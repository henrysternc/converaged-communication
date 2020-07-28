package test.stone.communication.dao;

import test.stone.communication.entity.LinkStatus;

public interface LinkStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LinkStatus record);

    int insertSelective(LinkStatus record);

    LinkStatus selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LinkStatus record);

    int updateByPrimaryKey(LinkStatus record);
}