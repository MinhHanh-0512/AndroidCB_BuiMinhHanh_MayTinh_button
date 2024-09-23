package com.example.mt_tay;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText display;
    private String currentExpression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        int[] buttonIds = {
                R.id.buttonClear, R.id.buttonOpen, R.id.buttonClose, R.id.buttonDelete,
                R.id.button0, R.id.button1, R.id.button2, R.id.buttonDot,
                R.id.button3, R.id.button4, R.id.button5, R.id.buttonDivide,
                R.id.button6, R.id.button7, R.id.button8, R.id.buttonMultiply,
                R.id.button9, R.id.buttonEqual, R.id.buttonAdd, R.id.buttonSubtract
        };

        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(this::onButtonClick);
        }
    }

    private void onButtonClick(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        switch (buttonText) {
            case "C":
                currentExpression = "";
                display.setText("");
                break;
            case "DEL":
                if (currentExpression.length() > 0) {
                    currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    display.setText(currentExpression);
                }
                break;
            case "=":
                calculate();
                break;
            default:
                currentExpression += buttonText;
                display.setText(currentExpression);
                break;
        }
    }

    private void calculate() {
        try {
            double result = eval(currentExpression);
            display.setText(String.valueOf(result));
            currentExpression = ""; // Reset sau khi tính
        } catch (Exception e) {
            display.setText("Lỗi");
            currentExpression = "";
        }
    }

    private double eval(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char token = expression.charAt(i);

            if (token == ' ') {
                continue; // Bỏ qua khoảng trắng
            }

            if (Character.isDigit(token) || token == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                values.push(Double.parseDouble(sb.toString()));
                i--; // Điều chỉnh lại chỉ số
            } else if (token == '(') {
                ops.push(token);
            } else if (token == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop(); // Bỏ dấu '('
            } else if (token == '+' || token == '-' || token == '*' || token == '/') {
                while (!ops.isEmpty() && hasPrecedence(token, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(token);
            }
        }

        while (!ops.isEmpty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Chia cho 0");
                return a / b;
        }
        return 0;
    }
}
