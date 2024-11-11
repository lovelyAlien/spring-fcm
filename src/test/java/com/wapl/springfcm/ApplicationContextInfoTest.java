package com.wapl.springfcm;

import com.wapl.springfcm.config.FirebaseMessagingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("local")
public class ApplicationContextInfoTest {

  AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(FirebaseMessagingConfig.class);

  @Test
  @DisplayName("모든 빈 출력하기")
  void findAllBean() {
    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    // "iter" 입력 후 탭 하면 향상된 for문 완성됌
    for (String beanDefinitionName : beanDefinitionNames) {
      Object bean = ac.getBean(beanDefinitionName);
      System.out.println("name = " + beanDefinitionName + " object = " + bean);
    }
  }

  @Test
  @DisplayName("애플리케이션 빈 출력하기")
  void findApplcationBean() {
    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    // "iter" 입력 후 탭 하면 향상된 for문 완성됌
    for (String beanDefinitionName : beanDefinitionNames) {
      BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

      // Role_Application : 스프링 내부에서 뭔가 등록한 빈이 아니라 내가 애플리케이션 개발하기위해서 등록한 빈이나 외부 라이브러리
      // Role ROLE_INFRASTRUCTURE : 스프링 내부에서 사용하는 빈
      if(beanDefinition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE) {
        Object bean = ac.getBean(beanDefinitionName);
        System.out.println("name = " + beanDefinitionName + " object = " + bean);
      }
    }
  }
}

