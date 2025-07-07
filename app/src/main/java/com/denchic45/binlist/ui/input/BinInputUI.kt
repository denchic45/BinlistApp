package com.denchic45.binlist.ui.input

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.data.api.util.presentOrNull
import org.koin.androidx.compose.koinViewModel

@Composable
fun BinInputScreen(viewModel: BinInputViewModel = koinViewModel()) {
    val input = viewModel.input
    val isValid = viewModel.isValid
    val uiState by viewModel.binDetailsUiState.collectAsState()
    val clipboardManager = LocalClipboard.current

    BoxWithConstraints {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.height(this@BoxWithConstraints.maxHeight / 2)) {
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = {
                            if (it.length <= 8 && it.isDigitsOnly())
                                viewModel.onInputChange(it)
                        },
                        modifier = Modifier.width(214.dp),
                        trailingIcon = {
                            if (input.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onInputChange("") },
                                ) {
                                    Icon(Icons.Default.Clear, "")
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                "4571 7360",
                                modifier = Modifier.fillMaxWidth(),
                                style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = BinTransformation(),
                        singleLine = true,
                        isError = uiState == BinDetailsUiState.InputError,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = viewModel::onFindClick, enabled = isValid) {
                        Text("Найти")
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            when (val state = uiState) {
                BinDetailsUiState.None -> {
                    Text(
                        "Введите первые 6-8 цифр номера карты (BIN/IIN)",
                        style = MaterialTheme.typography.bodySmall
                    )
//                repeat(20) {
//                    ListItem(
//                        headlineContent = { Text("Sample") },
//                        overlineContent = { Text("overline") })
//                }
                }

                BinDetailsUiState.Loading -> {
                    Text("Загрузка", style = MaterialTheme.typography.bodySmall)
                }

                is BinDetailsUiState.Success -> BinDetailsItems(
                    state.bindDetails,
                    onCopy = {

                    },
                    onExternalOpen = {}
                )

                BinDetailsUiState.TooManyRequests -> {
                    Text(
                        "Превышен лимит запросов. Попробуйте еще раз позже",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                BinDetailsUiState.InputError -> {
                    Text(
                        "Убедитесь в правильности ввода",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                BinDetailsUiState.NoConnection -> {
                    Text(
                        "Ошибка соединения",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                BinDetailsUiState.NotFound -> {
                    Text(
                        "Ничего не найдено",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                BinDetailsUiState.ServerError -> {
                    Text(
                        "Ошибка на уровне сервера",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                BinDetailsUiState.UnknownError -> {
                    Text(
                        "Неизвестная ошибка",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun BinDetailsItems(
    state: BinDetailsResponse,
    onCopy: (String) -> Unit,
    onExternalOpen: () -> Unit
) {
    ListItem(
        headlineContent = { Text(state.scheme) },
        overlineContent = { Text("Платежная сеть") }
    )


    ListItem(
        headlineContent = {
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (state.type == "Debit") Color.Unspecified
                        else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    append("Дебетовая")
                }
                append(" / ")
                withStyle(
                    style = SpanStyle(
                        color = if (state.type == "Credit") Color.Unspecified
                        else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    append("Кредитная")
                }
            })
        },
        overlineContent = { Text("Тип карты") }
    )

    ListItem(
        headlineContent = {
            Text(
                text = state.bank.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        overlineContent = { Text("Банк") }
    )


    ListItem(
        headlineContent = { Text(state.brand) },
        overlineContent = { Text("Платежная система") }
    )

    ListItem(
        headlineContent = {
            state.prepaid.onPresent { prepaid ->
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = if (prepaid) Color.Unspecified
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append("Да")
                    }
                    append(" / ")
                    withStyle(
                        style = SpanStyle(
                            color = if (!prepaid) Color.Unspecified
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        append("Нет")
                    }
                })
            }.onMissing { Text("Неизвестно") }
        },
        overlineContent = { Text("Предоплаченная") }
    )


    var expanded by remember { mutableStateOf(false) }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        TODO()
    }

    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = {}
        ),
        headlineContent = {
            Row {
                Row(Modifier.weight(1f)) {
                    Text("Длина - ")
                    state.number.length.onPresent { length ->
                        Text(length.toString())
                    }.onMissing {
                        Text("неизвестно", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Row(Modifier.weight(1f)) {
                    Text("Lugn - ")
                    Text(buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = if (state.number.luhn.presentOrNull() == true)
                                    Color.Unspecified
                                else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append("Есть")
                        }
                        append(" / ")
                        withStyle(
                            style = SpanStyle(
                                color = if (state.number.luhn.presentOrNull() == false)
                                    Color.Unspecified
                                else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            append("Нет")
                        }
                    })
                }
            }
        },
        overlineContent = { Text("Номер карты") }
    )

    ListItem(
        headlineContent = { Text(state.country.emoji + " " + state.country.name) },
        overlineContent = { Text("Страна") },
        supportingContent = {
            Text(
                """
                        (широта: ${state.country.latitude}, долгота: ${state.country.longitude})
                    """.trimIndent()
            )
        }
    )

}

class BinTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(text.text.let {
                if (it.length > 4)
                    StringBuilder(it).insert(4, ' ').toString()
                else it
            }),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return if (offset > 4) offset + 1 else offset
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return if (offset > 4) offset - 1 else offset
                }
            }
        )
    }
}