package fun.fengwk.convention4j.springboot.starter.webflux.reactiveclient;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Stream;

/**
 * @author fengwk
 */
public class ReactiveClientRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes
            .fromMap(metadata.getAnnotationAttributes(EnableReactiveClients.class.getName()));
        if (annotationAttributes == null) {
            return;
        }

        // register by clients
        Stream.of(annotationAttributes.getClassArray("clients")).forEach(client -> {
            registerReactiveFeignClient(registry, client);
        });

        // register by scan
        String[] basePackages = annotationAttributes.getStringArray("basePackages");
        if (basePackages.length == 0) {
            basePackages = new String[]{ ClassUtils.getPackageName(metadata.getClassName()) };
        }

        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(ReactiveClient.class));

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                        "@ReactiveFeignClient can only be specified on an interface");

                    registerReactiveFeignClient(registry, annotationMetadata.getClassName());
                }
            }
        }
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    private void registerReactiveFeignClient(BeanDefinitionRegistry registry, Class<?> clientClass) {
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
            .genericBeanDefinition(ReactiveClientFactoryBean.class);
        definition.addPropertyValue("type", clientClass.getName());
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        String beanName = StringUtils.uncapitalize(clientClass.getSimpleName());
        registry.registerBeanDefinition(beanName, definition.getBeanDefinition());
    }

    private void registerReactiveFeignClient(BeanDefinitionRegistry registry, String className) {
        try {
            registerReactiveFeignClient(registry, Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
