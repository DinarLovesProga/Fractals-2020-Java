package ru.smak.gui;

import ru.smak.gui.graphics.FinishedListener;
import ru.smak.gui.graphics.FractalPainter;
import ru.smak.gui.graphics.SelectionPainter;
import ru.smak.gui.graphics.components.GraphicsPanel;
import ru.smak.gui.graphics.coordinates.CartesianScreenPlane;
import ru.smak.gui.graphics.coordinates.Converter;
import ru.smak.gui.graphics.fractalcolors.ColorScheme1;
import ru.smak.gui.graphics.fractalcolors.ColorScheme2;
import ru.smak.math.Mandelbrot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    GraphicsPanel mainPanel;

    static final Dimension MIN_SIZE = new Dimension(450, 350);
    static final Dimension MIN_FRAME_SIZE = new Dimension(600, 500);

    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(MIN_FRAME_SIZE);
        setTitle("Фракталы");

        mainPanel = new GraphicsPanel();

        mainPanel.setBackground(Color.WHITE);

        GroupLayout gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addComponent(mainPanel, (int)(MIN_SIZE.height*0.8), MIN_SIZE.height, GroupLayout.DEFAULT_SIZE)
                .addGap(4)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(4)
                .addGroup(gl.createParallelGroup()
                        .addComponent(mainPanel, MIN_SIZE.width, MIN_SIZE.width, GroupLayout.DEFAULT_SIZE)
                )
                .addGap(4)
        );
        pack();
        var plane = new CartesianScreenPlane(
                mainPanel.getWidth(),
                mainPanel.getHeight(),
                -2, 1, -1, 1
        );

        var m = new Mandelbrot();
        var c = new ColorScheme2();
        var fp = new FractalPainter(plane, m);
        fp.col = c;
        fp.addFinishedListener(new FinishedListener() {
            @Override
            public void finished() {
                mainPanel.repaint();
            }
        });
        mainPanel.addPainter(fp);
        var sp = new SelectionPainter(mainPanel.getGraphics());

        mainPanel.addComponentListener(new ComponentAdapter() {
            int wM = mainPanel.getWidth();
            int hM = mainPanel.getHeight();
            double XmaxPlane = plane.xMax;
            double XminPlane = plane.xMin;
            double YmaxPlane = plane.yMax;
            double YminPlane = plane.yMin;
            @Override
            public void componentResized(ComponentEvent e) {
                var kW = (float)mainPanel.getWidth()/(float)wM;
                var kH = (float)mainPanel.getHeight()/(float)hM;
                var ration0 = (float)mainPanel.getWidth()/(float)mainPanel.getHeight();
                var ration = kW/kH;
                if(kW<1 || kH<1){
                    if (ration0>=1){
                        /*if (kW<1){
                            plane.xMin = XminPlane - (1-kW)*(XmaxPlane-XminPlane)/2;
                            plane.xMax = XmaxPlane + (1-kW)*(XmaxPlane-XminPlane)/2;
                        }
                        else{
                            plane.xMin = XminPlane - (ration-1)*(XmaxPlane-XminPlane)/2;
                            plane.xMax = XmaxPlane + (ration-1)*(XmaxPlane-XminPlane)/2;
                        }
                        if(kH<1){
                            plane.yMin = YminPlane -(1-ration)*(YmaxPlane-YminPlane)/2;
                            plane.yMax = YmaxPlane + (1-ration)*(YmaxPlane-YminPlane)/2;
                        }
                        else{
                            plane.yMin = YminPlane - (1-ration)*(YmaxPlane-YminPlane)/2;
                            plane.yMax = YmaxPlane + (1-ration)*(YmaxPlane-YminPlane)/2;
                        }*/
                        plane.yMin = YminPlane;
                        plane.yMax = YmaxPlane;
                        plane.xMin = XminPlane - Math.abs((1-ration)*(XmaxPlane-XminPlane)/2);
                        plane.xMax = XmaxPlane + Math.abs((1-ration)*(XmaxPlane-XminPlane)/2);
                    }
                    else{
                        /*if (kW<1){
                            plane.xMin = XminPlane;
                            plane.xMax = XmaxPlane;
                        }
                        else{
                            plane.xMin = XminPlane - (ration-1)*(XmaxPlane-XminPlane)/2;
                            plane.xMax = XmaxPlane + (ration-1)*(XmaxPlane-XminPlane)/2;
                        }
                        if(kH<1){
                            plane.yMin = YminPlane ;
                            plane.yMax = YmaxPlane ;
                        }
                        else{
                            plane.yMin = YminPlane - (1-ration)*(YmaxPlane-YminPlane)/2;
                            plane.yMax = YmaxPlane + (1-ration)*(YmaxPlane-YminPlane)/2;
                        }*/
                        plane.xMin = XminPlane;
                        plane.xMax = XmaxPlane;
                        plane.yMax = YmaxPlane + Math.abs((1/ration-1)*(YmaxPlane-YminPlane)/2);
                        plane.yMin = YminPlane - Math.abs((1/ration-1)*(YmaxPlane-YminPlane)/2);
                    }
                }
                else{
                    plane.xMin = XminPlane - (kW-1)*(XmaxPlane-XminPlane)/2;
                    plane.xMax = XmaxPlane + (kW-1)*(XmaxPlane-XminPlane)/2;
                    plane.yMin = YminPlane - (kH-1)*(YmaxPlane-YminPlane)/2;
                    plane.yMax = YmaxPlane + (kH-1)*(YmaxPlane-YminPlane)/2;
                }
                plane.setWidth(mainPanel.getWidth());
                plane.setHeight(mainPanel.getHeight());
                sp.setGraphics(mainPanel.getGraphics());
                mainPanel.repaint();

            }
        });
        mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                sp.setVisible(true);
                sp.setStartPoint(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                sp.setVisible(false);
                var r = sp.getSelectionRect();
                var xMin = Converter.xScr2Crt(r.x,plane);
                var xMax = Converter.xScr2Crt(r.x+r.width,plane);

                var yMin = Converter.yScr2Crt(r.y+r.height,plane);
                var yMax = Converter.yScr2Crt(r.y,plane);

                var pWidh =  xMax - xMin;
                var pHaight = yMax - yMin;
                var pRatio = (float)plane.getHeight()/(float)plane.getWidth();
                if (pWidh*pRatio>pHaight){
                    var pNewHaight = pWidh*pRatio;
                    plane.xMin = xMin;
                    plane.yMin = yMin-Math.abs((pNewHaight-pHaight)/2);
                    plane.xMax = xMin+pWidh;
                    plane.yMax = yMin+pNewHaight-Math.abs((pNewHaight-pHaight)/2);
                }
                else{
                    var pNewWidh = pHaight/pRatio;
                    plane.xMin = xMin - Math.abs((pNewWidh-pWidh)/2);
                    plane.yMin = yMin;
                    plane.xMax = xMin+pNewWidh-Math.abs((pNewWidh-pWidh)/2);
                    plane.yMax = yMin+pHaight;
                }

                mainPanel.repaint();
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                sp.setCurrentPoint(e.getPoint());
            }
        });
    }
}
