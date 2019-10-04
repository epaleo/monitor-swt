/**
 * MonitorWidgets.java 04-10-2019
 *
 * Copyright 2019 INDITEX.
 * Departamento de Sistemas
 */
package com.inditex.swt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class MonitorWidgets
 *
 * Monitor all widgets composites to get name about instances in a Tree.
 * It displays information moving cursor over component and then pressing <code>Ctrl+Shitf+A</code>
 *
 * @author <a href="mailto:emiliop@ext.inditex.com">Emilio Paleo Barreira</a>
 */
@Aspect
public class MonitorWidgets {
    private static Logger LOGGER = LoggerFactory.getLogger(MonitorWidgets.class);

    private Widget lastWidget;
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    @Pointcut("execution(* org.eclipse.swt.widgets.EventTable.sendEvent(org.eclipse.swt.widgets.Event)) && args(event)")
    public void sendEventCut(Event event) {
    }

    @Pointcut("execution(org.eclipse.swt.widgets.Control+.new(org.eclipse.swt.widgets.Composite, int))")
    public void newControl() {
    }

    /**
     * Write to logger name class of the widget with mouse over
     *
     * @param joinPoint the join point
     * @param event the event
     * @return data from the method call
     * @throws Throwable errors when there is a problem
     */
    @Around("sendEventCut(event)")
    public Object sendEventAspect(ProceedingJoinPoint joinPoint, Event event) throws Throwable {
        LOGGER.trace("Invoked event: type:{}, character: {}", event.type, event.character);
        lock.writeLock().lock();
        Object ignoredToStringResult = joinPoint.proceed();
        try {
            if (event.type == SWT.MouseHover) {
                lastWidget = event.widget;
            }
            if (event.type == SWT.MouseExit) {
                lastWidget = null;
            }
            if ((event.type == SWT.KeyDown) && (event.stateMask & (SWT.CTRL | SWT.SHIFT)) == (SWT.CTRL | SWT.SHIFT) && (event.keyCode == 'a')) {
                if (lastWidget != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("\n");
                    sb.append("######## CURSOR ON COMPONENT ########");
                    sb.append("\n");
                    List<String> lParent = getParentHierarchy(lastWidget);
                    StringBuilder tabulatorsSb = new StringBuilder();
                    Collections.reverse(lParent);
                    lParent.forEach(nameClass -> {
                        sb.append(tabulatorsSb.toString());
                        sb.append(nameClass);
                        sb.append("\n");
                        tabulatorsSb.append("\t");
                    });

                    sb.append(tabulatorsSb.toString());
                    sb.append(lastWidget.getClass());
                    sb.append(" -> ");
                    sb.append(lastWidget.toString());
                    sb.append("\n");
                    sb.append("#####################################\n");
                    LOGGER.debug("{}", sb);
                }
            }
        } catch (Throwable ex ){
            LOGGER.error("Error new event", ex);
        } finally {
            lock.writeLock().unlock();
        }
        return ignoredToStringResult;
    }

    /**
     * Add {@link MouseTrackListener} to Control instances
     *
     * @param joinPoint the join point
     * @return data from the method call
     * @throws Throwable throwable when an error occurs
     */
    @Around("newControl()")
    public Object newControlAround(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.trace("Invoked event: newControlAround");
        Object result = joinPoint.proceed();
        try {
            ((Control) joinPoint.getThis()).addMouseTrackListener(new MouseTrackListener() {
                @Override
                public void mouseEnter(MouseEvent mouseEvent) {

                }

                @Override
                public void mouseExit(MouseEvent mouseEvent) {
                    lock.writeLock().lock();
                    try {
                        lastWidget = null;
                    } finally {
                        lock.writeLock().unlock();
                    }
                }

                @Override
                public void mouseHover(MouseEvent mouseEvent) {
                    lock.writeLock().lock();
                    try {
                        lastWidget = mouseEvent.widget;
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            });
        }catch (Throwable ex){
            LOGGER.error("Error add listener when create instance",ex);
        }
        return result;
    }

    /**
     * Get parent hierarchy
     *
     * @param value component to get parent hierarchy
     * @return hierarchy list of components
     */
    private List<String> getParentHierarchy(Object value) {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();
            Object parent = null;

            if (value instanceof Control && !(value instanceof Shell)) {
                parent = ((Control) value).getShell();
            }else if (value instanceof Shell){
                result.add(((Shell)value).getData()!= null ? ((Shell)value).getData().toString() : value.toString());
                parent = ((Shell)value).getParent();
            }

            if (parent == null){
                return result;
            }else{
                List<String> resultParent = getParentHierarchy(parent);
                result.addAll(resultParent);
                return result;
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}
