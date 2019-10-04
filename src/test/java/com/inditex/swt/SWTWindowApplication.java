package com.inditex.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class SWTWindowApplication {


    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);

        RowLayout layout = new RowLayout();
        layout.wrap = true;
        shell.setLayout(layout);

        new Button(shell, SWT.PUSH).setText("B1");
        new Button(shell, SWT.PUSH).setText("Wide Button 2");
        Button b = new Button(shell, SWT.SELECTED);
        b.setText("Button 3");
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                callMethod();
            }
        });

        shell.pack();
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }

    private static void callMethod() {
        System.out.println("TEST");
    }
}
