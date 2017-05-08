package cn.sinjinsong.skeleton.dao.mail;

import cn.sinjinsong.skeleton.domain.entity.mail.MailDO;
import cn.sinjinsong.skeleton.enumeration.mail.MailStatus;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MailDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mail
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mail
     *
     * @mbggenerated
     */
    int insert(MailDO record);

    
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table mail
     *
     * @mbggenerated
     */
    MailDO selectByPrimaryKey(Long id);
    Page<MailDO> findBySender(@Param("sender") Long id, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    Page<MailDO> findByReceiver(@Param("receiver") Long id, @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize, @Param("mailStatus") MailStatus mailStatus);
    void updateStatus(@Param("ids") List<Long> ids, @Param("mailStatus") MailStatus mailStatus);
    void insertBatch(@Param("mails") List<MailDO> mails);
}