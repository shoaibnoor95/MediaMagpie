package de.wehner.mediamagpie.conductor.spring.deploy;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;

import de.wehner.mediamagpie.conductor.spring.deploy.DeployMode.DeployModeType;

/**
 * Sets an custom {@link AutowireCandidateResolver} to decide based on the deployment mode if the object is a candidate or not
 * 
 */
public class DeployModeAutowireConfigurer implements BeanFactoryPostProcessor, Ordered, BeanClassLoaderAware {

    private int _order = Ordered.LOWEST_PRECEDENCE;
    protected ClassLoader _beanClassloader;

    public void setOrder(int order) {
        _order = order;
    }

    public int getOrder() {
        return _order;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            throw new IllegalStateException("DeployModeAutowireConfigurer needs to operate on a DefaultListableBeanFactory");
        }
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        DeployModeAnnotationAutowireCandidateResolver resolver = new DeployModeAnnotationAutowireCandidateResolver();
        dlbf.setAutowireCandidateResolver(resolver);
    }

    class DeployModeAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {

        private DeployModeType _deployMode;

        public DeployModeAnnotationAutowireCandidateResolver() {
            String deployModeSystemProperty = System.getProperty(DeployMode.KEY);
            if (StringUtils.isEmpty(deployModeSystemProperty)) {
                throw new IllegalStateException("no deploy mode set. specify with -D" + DeployMode.KEY);
            }
            _deployMode = DeployModeType.lookup(deployModeSystemProperty);
            if (_deployMode == null) {
                throw new IllegalStateException("specified deploy mode '" + deployModeSystemProperty + "' does not match with one of existing in "
                        + DeployModeType.class.getName() + ":" + Arrays.asList(DeployModeType.values()));
            }
        }

        @Override
        public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
            boolean autowireCandidate = super.isAutowireCandidate(bdHolder, descriptor);
            if (autowireCandidate) {
                RootBeanDefinition beanDefinition = (RootBeanDefinition) bdHolder.getBeanDefinition();
                try {
                    Class<?> beanClass = beanDefinition.resolveBeanClass(_beanClassloader);
                    DeployMode deployMode = beanClass.getAnnotation(DeployMode.class);
                    if (deployMode != null) {
                        if (deployMode.value().equals(_deployMode) || deployMode.value() == DeployModeType.ANY) {
                            return true;
                        }
                        return false;
                    }
                    return autowireCandidate; // what is true in this case
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("bean class [" + beanDefinition.getBeanClassName() + "] can not be resolved.");
                }
            }
            return false;
        }
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        _beanClassloader = classLoader;
    }
}