package org.openmole.commons.aspect.eventdispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.InterruptedException;
import java.lang.reflect.Method;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.annotation.Annotation;

public aspect EventAspect {

	before() : execution(* *(..)) && @annotation(org.openmole.commons.aspect.eventdispatcher.BeforeObjectModified) {

		Object object = thisJoinPoint.getTarget();

                Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
                BeforeObjectModified annotation = method.getAnnotation(BeforeObjectModified.class);

                String type = annotation.name();
                Object[] args = thisJoinPoint.getArgs();

                EventDispatcher.<Object>objectChanged(object,type, args);
	}


	after() : execution(* *(..)) && @annotation(org.openmole.commons.aspect.eventdispatcher.ObjectModified) {
		
		Object object = thisJoinPoint.getTarget();

                Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
                ObjectModified annotation = method.getAnnotation(ObjectModified.class);

                EventDispatcher.<Object>objectChanged(object,annotation.name(), thisJoinPoint.getArgs());
	}

        after() : execution(*.new(..)) && @annotation(org.openmole.commons.aspect.eventdispatcher.ObjectConstructed) {

		// construct genericIdentifier
		Object object = thisJoinPoint.getTarget();
		//Object ret = proceed();

               // Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
               // ObjectModified annotation = method.getAnnotation(ObjectModified.class);

               // Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,"ctr: " + thisJoinPointStaticPart.getSignature().toString());

                EventDispatcher.objectConstructed(object);
		//Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO,object.toString() + " "+ method);

		//return ret;
	}
}
