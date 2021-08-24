package org.jzz.mybatistest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.jzz.mybatistest.entity.MyBean;
import org.jzz.mybatistest.mapper.TestMapper;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class App {

    /** 使用xml构建SqlSessionFactory */
    static SqlSessionFactory getFactory() throws Exception{
        //方法1:不手动绑定properties文件,在xml中绑定(或者根本不绑定,将配置直接写在xml中)
//        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));

        //方法2:在代码中绑定properties文件，优先级顺序最高
        Properties properties = new Properties();
        properties.load(Resources.getResourceAsStream("datasource.properties"));
        SqlSessionFactory factory =
              new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"), properties);

        return factory;
    }

    /** 纯代码构建SqlSessionFactory 不依赖xml */
    static SqlSessionFactory getFactory2() throws Exception{
        Properties properties = new Properties();
        properties.load(Resources.getResourceAsStream("datasource.properties"));
        DataSourceFactory dataSourceFactory = new PooledDataSourceFactory();
        dataSourceFactory.setProperties(properties);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSourceFactory.getDataSource());
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TestMapper.class);  //命名空间是啥??
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    static void testXmlMapper() throws Exception {
        SqlSession sqlSession = getFactory().openSession();
        MyBean myBean = sqlSession.selectOne("xml.mapper.TestMapper.selectTest", 1);  //xml中用resource注册的xml类型Mapper
        System.out.println(myBean);

        TestMapper mapper = sqlSession.getMapper(TestMapper.class);     //xml中注册class类型的Mapper，也可以这么用
//        MyBean myBean2 = sqlSession.selectOne("org.jzz.mybatiestest.mapper.TestMapper.selectTest", 1);  //不能用命名空间得到xml注册的class类的Mapper！！测试不成功
        MyBean myBean2 = mapper.selectTest(1);
        System.out.println(myBean2);
    }

    static void testAnnotationMapper() throws Exception {
        SqlSessionFactory sqlSessionFactory = getFactory(); //SqlSessionFactory应该在运行期间保持唯一,没有任何理由丢弃或重建
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.addMapper(TestMapper.class);  //在configuration对象中主动添加Mapper注解类
        SqlSession sqlSession = sqlSessionFactory.openSession();    //每个线程的sqlSession应该独立,非线程安全
        TestMapper mapper = sqlSession.getMapper(TestMapper.class);
        MyBean myBean = mapper.selectTest(1);
        System.out.println(myBean);

        SqlSession sqlSession2 = getFactory2().openSession();
//        MyBean myBean = sqlSession.selectOne("org.jzz.mybatiestest.mapper.TestMapper.selectTest", 1); //不能用命名空间得到代码注册的class类的Mapper！！测试不成功
        MyBean myBean2 = (MyBean) sqlSession2.getMapper(TestMapper.class).selectTest(1);
        System.out.println(myBean);
    }

    static void testInsert() throws Exception {
        SqlSession sqlSession = getFactory().openSession();
        MyBean bean = MyBean.getRandomBean();
        System.out.println(bean);
        int value = sqlSession.insert("xml.mapper.TestMapper.insertTest", bean);
        sqlSession.commit();    //提交
        System.out.println(value);
    }

    static void testInsertList() throws Exception {
        SqlSession sqlSession = getFactory().openSession();
        List<MyBean> myBeans = Arrays.asList(MyBean.getRandomBean(), MyBean.getRandomBean());
        int value = sqlSession.insert("xml.mapper.TestMapper.insertMyBeans", myBeans);
        sqlSession.commit();    //提交
        System.out.println(value);
    }

    public static void main(String[] args) throws Exception{
//        testXmlMapper();
//        testAnnotationMapper();
//        testInsert();
        testInsertList();
    }
}
