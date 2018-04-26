import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.Locale;

public class SpringApplet extends JApplet implements MouseListener, MouseMotionListener, ActionListener {
    private SimTask simTask;
    private SimEngine simEngine;
    private Timer timer;
    private boolean isMouseDragged;

    private TextField mass;
    private TextField springConstant;
    private TextField dampingRatio;
    private TextField springLength;
    private TextField gravitationalAcceleration;
    private Button reset;

    // Metoda zmieniająca format liczby zmiennoprzecinkowej
    public static String format(double number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.UK);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("#.##");
        return decimalFormat.format(number);
    }

    // Metoda rysująca linię reprezentującą sprężynę
    public void createSpring(Graphics2D graphics2D, Vector2D suspensionPoint, Vector2D position) {
        double x1 = suspensionPoint.x;
        double y1 = suspensionPoint.y;
        double x2 = position.x;
        double y2 = position.y;

        Line2D line = new Line2D.Double(x1, y1, x2, y2);
        graphics2D.draw(line);
    }

    // Metoda inicjalizująca symulację
    @Override
    public void init() {
        double mass = 20.0;
        double springConstant = 3.0;
        double dampingRatio = 0.3;
        double springLength = 200.0;
        Vector2D position = new Vector2D(600.0, 300.0);
        Vector2D velocity = new Vector2D(0.0, 0.0);
        Vector2D suspensionPoint = new Vector2D(400.0, 50.0);
        double gravitationalAcceleration = 9.81;

        this.simEngine = new SimEngine(mass, springConstant, dampingRatio, springLength, position, velocity,
                suspensionPoint, gravitationalAcceleration);

        this.simTask = new SimTask(this, simEngine, 0.1);
        this.timer = new Timer();
        this.isMouseDragged = false;

        this.timer.scheduleAtFixedRate(simTask, 1000, 10);

        addMouseListener(this);
        addMouseMotionListener(this);

        this.reset = new Button("Reset");
        this.reset.addActionListener(this);
        this.add(reset);

        this.mass = new TextField("" + mass, 4);
        this.springConstant = new TextField("" + springConstant, 4);
        this.dampingRatio = new TextField("" + dampingRatio, 4);
        this.springLength = new TextField("" + springLength, 4);
        this.gravitationalAcceleration = new TextField("" + gravitationalAcceleration, 4);

        this.add(this.mass);
        this.add(this.springConstant);
        this.add(this.dampingRatio);
        this.add(this.springLength);
        this.add(this.gravitationalAcceleration);
    }

    // Metoda przedstawiająca obraz symulacji
    @Override
    public void paint(Graphics graphics) {
        this.setSize(800, 600);
        this.setBackground(Color.white);

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.clearRect(0, 0, this.getWidth(), this.getHeight());
        graphics2D.setPaint(Color.black);
        Vector2D position = simEngine.getPosition();
        Vector2D suspensionPoint = simEngine.getSuspensionPoint();

        this.createSpring(graphics2D, suspensionPoint, position);

        graphics2D.fillOval((int) position.x - 10, (int) position.y - 10, 20, 20);

        this.mass.setBounds(30, 40, 50, 20);
        this.springConstant.setBounds(30, 80, 50, 20);
        this.dampingRatio.setBounds(30, 120, 50, 20);
        this.springLength.setBounds(30, 160, 50, 20);
        this.gravitationalAcceleration.setBounds(30, 200, 50, 20);
        this.reset.setBounds(30, 240, 50, 30);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Vector2D position = simEngine.getPosition();
        boolean conditionX = e.getX() >= (int) position.x - 25 && e.getX() <= (int) position.x + 25;
        boolean conditionY = e.getY() >= (int) position.y - 25 && e.getY() <= (int) position.y + 25;

        if (conditionX && conditionY) {
            this.timer.cancel();
            this.simEngine.resetSimulation();
            this.isMouseDragged = true;
        }

        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.isMouseDragged) {
            this.simTask = new SimTask(this, this.simEngine, 0.1);
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(simTask, 1000, 10);
            this.isMouseDragged = false;
        }

        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.isMouseDragged) {
            Vector2D position = this.simEngine.getPosition();
            position.x = e.getX();
            position.y = e.getY();
            repaint();
        }

        e.consume();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != this.reset) {
            return;
        }

        this.timer.cancel();

        double mass = Double.parseDouble(this.mass.getText());
        double springConstant = Double.parseDouble(this.springConstant.getText());
        double dampingRatio = Double.parseDouble(this.dampingRatio.getText());
        double springLength = Double.parseDouble(this.springLength.getText());
        double gravitationalAcceleration = Double.parseDouble(this.gravitationalAcceleration.getText());

        Vector2D position = new Vector2D(600.0, 300.0);
        Vector2D velocity = new Vector2D(0.0, 0.0);
        Vector2D suspensionPoint = new Vector2D(400.0, 50.0);

        this.simEngine = new SimEngine(mass, springConstant, dampingRatio, springLength, position, velocity,
                suspensionPoint, gravitationalAcceleration);

        this.simTask = new SimTask(this, simEngine, 0.1);
        this.timer = new Timer();

        this.timer.scheduleAtFixedRate(simTask, 1000, 10);
        this.repaint();
    }
}
