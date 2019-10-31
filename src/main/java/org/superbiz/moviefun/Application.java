package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }

    @Bean
    public DatabaseServiceCredentials actionDatabaseServiceCredentials(@Value("${VCAP_SERVICES}") String vcapServices){
        return new DatabaseServiceCredentials(vcapServices);
    }

    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return hikariDataSource;
    }

    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource);
        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter n = new HibernateJpaVendorAdapter();
        n.setDatabase(Database.MYSQL);
        n.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        n.setGenerateDdl(true);
        return n;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalAlbumsBean(DataSource albumsDataSource, HibernateJpaVendorAdapter hiber){
        LocalContainerEntityManagerFactoryBean local = new LocalContainerEntityManagerFactoryBean();
        local.setDataSource(albumsDataSource);
        local.setJpaVendorAdapter(hiber);
        local.setPackagesToScan("org.superbiz.moviefun.albums");
        local.setPersistenceUnitName("albums");
        return local;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getLocalMoviesBean(DataSource moviesDataSource, HibernateJpaVendorAdapter hiber){
        LocalContainerEntityManagerFactoryBean local = new LocalContainerEntityManagerFactoryBean();
        local.setDataSource(moviesDataSource);
        local.setJpaVendorAdapter(hiber);
        local.setPackagesToScan("org.superbiz.moviefun.movies");
        local.setPersistenceUnitName("movies");
        return local;
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManagerAlbums(EntityManagerFactory getLocalAlbumsBean){
        JpaTransactionManager jpa = new JpaTransactionManager();
        jpa.setEntityManagerFactory(getLocalAlbumsBean);
        return jpa;
    }

    @Bean
    public PlatformTransactionManager getPlatformTransactionManagerMovies(EntityManagerFactory getLocalMoviesBean){
        JpaTransactionManager jpa = new JpaTransactionManager();
        jpa.setEntityManagerFactory(getLocalMoviesBean);
        return jpa;
    }


}