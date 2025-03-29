import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class HorseGame extends JPanel implements ActionListener, KeyListener {

    private int horseY = 250;
    private int horseVelocity = 0;
    private boolean isJumping = false;
    private ArrayList<Rectangle> obstacles;
    private Timer timer;
    private Random rand;
    private int score = 0;
    private boolean gameOver = false;

    public HorseGame() {
        setPreferredSize(new Dimension(800, 400));
        setBackground(new Color(135, 206, 235)); // Céu azul
        timer = new Timer(16, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);

        obstacles = new ArrayList<>();
        rand = new Random();
        spawnObstacle();
    }

    private void spawnObstacle() {
        int width = 30 + rand.nextInt(30); // Largura variável
        int height = 40 + rand.nextInt(20); // Altura variável
        int x = 800 + rand.nextInt(300); // Posição aleatória após o fim da tela
        obstacles.add(new Rectangle(x, 320 - height, width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenha o chão
        g.setColor(new Color(34, 139, 34)); // Verde para o chão
        g.fillRect(0, 320, 800, 80);

        // Desenha o cavalo
        g.setColor(new Color(139, 69, 19)); // Marrom para o cavalo
        g.fillRect(50, horseY, 50, 40);
        g.setColor(Color.BLACK);
        g.fillRect(55, horseY + 35, 5, 10); // Perna 1
        g.fillRect(70, horseY + 35, 5, 10); // Perna 2
        g.fillRect(60, horseY + 25, 8, 8); // Cabeça

        // Desenha obstáculos
        g.setColor(Color.RED);
        for (Rectangle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // Desenha pontuação
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 600, 30);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over!", 300, 200);
            g.drawString("Press SPACE to Restart", 220, 250);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Movimento dos obstáculos
            Iterator<Rectangle> it = obstacles.iterator();
            while (it.hasNext()) {
                Rectangle obstacle = it.next();
                obstacle.x -= 6; // Velocidade do obstáculo
                if (obstacle.x + obstacle.width < 0) {
                    it.remove();
                    spawnObstacle();
                    score++; // Aumenta a pontuação ao ultrapassar um obstáculo
                }
                // Detecção de colisão
                if (new Rectangle(50, horseY, 50, 40).intersects(obstacle)) {
                    gameOver = true;
                    timer.stop();
                    repaint();
                }
            }

            // Física do pulo (gravidade + impulso)
            if (isJumping) {
                horseVelocity += 1;
                horseY += horseVelocity;
                if (horseY >= 250) {
                    horseY = 250;
                    isJumping = false;
                }
            }

            repaint();
        }
    }

    private void resetGame() {
        horseY = 250;
        horseVelocity = 0;
        obstacles.clear();
        spawnObstacle();
        isJumping = false;
        score = 0;
        gameOver = false;
        timer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
            } else if (!isJumping) {
                horseVelocity = -15; // Impulso inicial para cima
                isJumping = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Horse Game");
        HorseGame game = new HorseGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
