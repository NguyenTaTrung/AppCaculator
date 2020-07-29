package sun.trainingcourse.appcaculator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAction()
    }

    private fun initAction() {
        buttonZero.setOnClickListener { appendEditTextMath("0", false) }
        buttonOne.setOnClickListener { appendEditTextMath("1", false) }
        buttonTwo.setOnClickListener { appendEditTextMath("2", false) }
        buttonThree.setOnClickListener { appendEditTextMath("3", false) }
        buttonFour.setOnClickListener { appendEditTextMath("4", false) }
        buttonFive.setOnClickListener { appendEditTextMath("5", false) }
        buttonSix.setOnClickListener { appendEditTextMath("6", false) }
        buttonSeven.setOnClickListener { appendEditTextMath("7", false) }
        buttonEight.setOnClickListener { appendEditTextMath("8", false) }
        buttonNine.setOnClickListener { appendEditTextMath("9", false) }
        buttonDot.setOnClickListener { addDot() }

        buttonPlus.setOnClickListener { addOperator("+") }
        buttonMinus.setOnClickListener { addOperator("-") }
        buttonMulti.setOnClickListener { addOperator("*") }
        buttonDiv.setOnClickListener { addOperator("/") }

        buttonOpen.setOnClickListener { addOpen() }
        buttonClose.setOnClickListener { addClose() }

        buttonClear.setOnClickListener { resetAll() }
        buttonDelete.setOnClickListener { deleteElement() }

        buttonEqual.setOnClickListener { showResult() }
    }

    private fun isOperator(c: Char) = when(c) {
        '+', '-', '*', '/', '(', ')' -> true
        else -> false
    }

    private fun priority(c: Char): Int {
        return if (c == '+' || c == '-') 1
        else if (c == '*' || c == '/') 2
        else 0
    }

    private fun processString(text: String): Array<String> {
        var math = text
        var s = ""
        val elementMath: Array<String>
        math = math.trim()
        math = math.replace("\\s+".toRegex(), " ")
        for (element in math) {
            s = if (!isOperator(element)) s + element else "$s $element "
        }
        s = s.trim()
        s = s.replace("\\s+".toRegex(), " ")
        elementMath = s.split(" ").toTypedArray()
        return elementMath
    }

    private fun postfix(elementMath: Array<String>): Array<String> {
        var s = ""
        val elementPostfix: Array<String>
        val stack = Stack<String>()
        if (elementMath[0][0] == '-') s = "$s 0"
        for (i in elementMath.indices) {
            val c = elementMath[i][0]
            if (!isOperator(c))
                s = s + " " + elementMath[i]
            else {
                if (c == '(') stack.push(elementMath[i])
                else {
                    if (c == ')') {
                        var c1: Char
                        do {
                            c1 = stack.peek()[0]
                            if (c1 != '(') s = s + " " + stack.peek()
                            stack.pop()
                        } while (c1 != '(')
                    } else {
                        while (!stack.isEmpty() && priority(stack.peek()[0]) >= priority(c)) {
                            s = s + " " + stack.peek()
                            stack.pop()
                        }
                        stack.push(elementMath[i])
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            s = s + " " + stack.peek()
            stack.pop()
        }
        elementPostfix = s.split(" ").toTypedArray()
        return elementPostfix
    }

    private fun valueMath(elementPostfix: Array<String>): String {
        val stack = Stack<String>()
        for (i in 1 until elementPostfix.size) {
            val c = elementPostfix[i][0]
            if (!isOperator(c)) stack.push(elementPostfix[i]) else {
                val num1 = stack.pop().toFloat().toDouble()
                val num2 = stack.pop().toFloat().toDouble()
                val num = when (c) {
                    '+' -> num2 + num1
                    '-' -> num2 - num1
                    '*' -> num2 * num1
                    '/' -> num2 / num1
                    else -> 0.0
                }
                stack.push(num.toString())
            }
        }
        return stack.pop()
    }

    private fun addDot() {
        val value = editTextMath.text.toString()
        if (value.isNotEmpty()) {
            val c = value.substring(value.length - 1, value.length)[0]
            if (!isOperator(c)) {
                if (c != '.') {
                    appendEditTextMath(".", true)
                }
            } else {
                if (c == ')') {
                    editTextMath.append("*")
                }
                editTextMath.append("0.")
            }
        } else {
            editTextMath.append("0.")
        }
    }

    private fun addOpen() {
        val value = editTextMath.text.toString()
        if (value.isNotEmpty()) {
            val c = value.substring(value.length - 1, value.length)[0]
            if (!isOperator(c)) {
                appendEditTextMath("*(", true)
            } else {
                appendEditTextMath("(", true)
            }
        } else {
            appendEditTextMath("(", true)
        }
    }

    private fun addClose() {
        val value = editTextMath.text.toString()
        if (value.isNotEmpty()) {
            val c = value.substring(value.length - 1, value.length)[0]
            if (!isOperator(c)) {
                appendEditTextMath(")", true)
            }
        }
    }

    private fun deleteElement() {
        val txt = BaseInputConnection(editTextMath, true)
        txt.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
    }

    private fun resetAll() {
        textValue.text = ""
        editTextMath.text.clear()
    }

    private fun showResult() {
        val value = editTextMath.text.toString()
        if (value.isNotEmpty()) {
            val c = value.substring(value.length - 1, value.length)[0]
            if (!isOperator(c) || c == ')') {
                val text = editTextMath.text.toString()
                val array = postfix(processString(text))
                textValue.text = valueMath(array)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addOperator(operator: String) {
        val value = editTextMath.text.toString()
        if (value.isNotEmpty()) {
            val c = value.substring(value.length - 1, value.length)[0]
            if (!isOperator(c)) {
                appendEditTextMath(operator, true)
            } else {
                if (c == ')') {
                    appendEditTextMath(operator, true)
                } else {
                    editTextMath.setText(value.substring(0, value.length - 1) + operator)
                }
            }
        }
    }

    private fun appendEditTextMath(string: String, isOperator: Boolean) {
        if (textValue.text.isNotEmpty()) {
            editTextMath.text.clear()
        }

        if (!isOperator) {
            textValue.text = ""
            editTextMath.append(string)
        } else {
            editTextMath.append(textValue.text)
            editTextMath.append(string)
            textValue.text = ""
        }
    }
}
