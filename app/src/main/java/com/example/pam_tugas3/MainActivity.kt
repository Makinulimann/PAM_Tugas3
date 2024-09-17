package com.example.pam_tugas3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var resultTv: TextView
    private var currentExpression = StringBuilder()
    private var lastNumeric: Boolean = false // Untuk melacak apakah input terakhir adalah angka
    private var lastDot: Boolean = false // Untuk melacak apakah titik sudah ditambahkan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTv = findViewById(R.id.resultTv)

        // Menghubungkan tombol-tombol dengan fungsinya
        val buttons = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (buttonId in buttons) {
            findViewById<Button>(buttonId).setOnClickListener { onNumberClick(it as Button) }
        }

        val operators = arrayOf(
            R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide
        )

        for (buttonId in operators) {
            findViewById<Button>(buttonId).setOnClickListener { onOperatorClick(it as Button) }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener { clearResult() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
    }

    private fun onNumberClick(button: Button) {
        currentExpression.append(button.text)
        resultTv.text = currentExpression.toString()
        lastNumeric = true
        lastDot = false // Reset flag saat angka baru dimasukkan
    }

    private fun onOperatorClick(button: Button) {
        if (lastNumeric) {
            currentExpression.append(button.text)
            resultTv.text = currentExpression.toString()
            lastNumeric = false
            lastDot = false // Reset flag titik setelah operator dimasukkan
        }
    }

    private fun clearResult() {
        currentExpression.clear()
        resultTv.text = "0"
        lastNumeric = false
        lastDot = false
    }

    private fun calculateResult() {
        if (lastNumeric) {
            try {
                val result = evaluateExpression(currentExpression.toString())
                resultTv.text = formatResult(result) // Memformat hasil
                currentExpression.clear()
                currentExpression.append(result)
            } catch (e: Exception) {
                resultTv.text = "Error"
                currentExpression.clear()
            }
        }
    }

    private fun evaluateExpression(expression: String): Double {
        val tokens = tokenizeExpression(expression)
        return evaluateTokens(tokens)
    }

    private fun tokenizeExpression(expression: String): List<String> {
        return expression.replace(Regex("([+\\-*/])"), " $1 ")
            .trim()
            .split("\\s+".toRegex())
    }

    private fun evaluateTokens(tokens: List<String>): Double {
        val numbers = Stack<Double>()
        val operators = Stack<String>()

        for (token in tokens) {
            when {
                token.matches(Regex("[0-9]+")) -> numbers.push(token.toDouble()) // Konversi ke Double
                token in setOf("+", "-", "*", "/") -> {
                    while (operators.isNotEmpty() && hasPrecedence(token, operators.peek())) {
                        applyOperator(numbers, operators.pop())
                    }
                    operators.push(token)
                }
            }
        }

        while (operators.isNotEmpty()) {
            applyOperator(numbers, operators.pop())
        }

        return if (numbers.isNotEmpty()) numbers.pop() else 0.0
    }


    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op2 == "(" || op2 == ")") return false
        if ((op1 == "*" || op1 == "/") && (op2 == "+" || op2 == "-")) return false
        return true
    }

    private fun applyOperator(numbers: Stack<Double>, operator: String) {
        if (numbers.size < 2) return
        val b = numbers.pop() // Operand 2
        val a = numbers.pop() // Operand 1

        when (operator) {
            "+" -> numbers.push(a + b)
            "-" -> numbers.push(a - b)
            "*" -> numbers.push(a * b)
            "/" -> {
                if (b != 0.0) {
                    numbers.push(a / b)
                } else {
                    throw ArithmeticException("Division by zero")
                }
            }
        }
    }


    // Memformat hasil agar desimal tidak ditampilkan jika hasil merupakan bilangan bulat
    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString() // Tampilkan sebagai bilangan bulat
        } else {
            result.toString() // Tampilkan desimal jika perlu
        }
    }
}
