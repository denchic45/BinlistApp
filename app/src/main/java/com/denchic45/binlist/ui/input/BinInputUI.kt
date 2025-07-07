package com.denchic45.binlist.ui.input

import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.outlined.Launch
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import com.denchic45.binlist.data.api.bin.model.BinDetailsResponse
import com.denchic45.binlist.ui.theme.disabledTextColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BinInputScreen(
    viewModel: BinInputViewModel = koinViewModel(),
    navigateToHistory: () -> Unit
) {
    val input = viewModel.input
    val isValid = viewModel.isValid
    val uiState by viewModel.binDetailsUiState.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}
    val coroutine = rememberCoroutineScope()
    BoxWithConstraints {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = { Text("BinList") },
                actions = {
                    IconButton(onClick = navigateToHistory) {
                        Icon(Icons.Outlined.History, null)
                    }
                }
            )
            Column(modifier = Modifier.height(this@BoxWithConstraints.maxHeight / 3)) {
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
                }

                BinDetailsUiState.Loading -> {
                    Text("Загрузка", style = MaterialTheme.typography.bodySmall)
                }

                is BinDetailsUiState.Success -> BinDetailsItems(
                    state.bindDetails,
                    onCopy = { text ->
                        coroutine.launch {
                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("text", text)))
                        }
                        Toast.makeText(context, "Скопировано в буфер обмена", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onUriOpen = { uri ->
                        launcher.launch(
                            Intent(Intent.ACTION_VIEW, uri.toUri())
                        )
                    }
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
    onUriOpen: (String) -> Unit
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = { onCopy(state.scheme) }
        ),
        headlineContent = { Text(state.scheme) },
        overlineContent = { Text("Платежная сеть") }
    )

    ListItem(
        headlineContent = {
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = if (state.type == "debit") Color.Unspecified
                        else disabledTextColor()
                    )
                ) { append("Дебетовая") }
                append(" / ")
                withStyle(
                    style = SpanStyle(
                        color = if (state.type == "credit") Color.Unspecified
                        else disabledTextColor()
                    )
                ) { append("Кредитная") }
            })
        },
        overlineContent = { Text("Тип карты") }
    )

    var bankDetailsExpanded by remember { mutableStateOf(false) }
    val elevation = animateDpAsState(
        targetValue = if (bankDetailsExpanded) 4.dp else 0.dp,
        label = "elevation"
    )
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween()
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween()
        )
    }


    Column(
        Modifier
            .graphicsLayer { this.shadowElevation = elevation.value.toPx() }
            .zIndex(if (bankDetailsExpanded) 1f else 0f)) {
        ListItem(
            modifier = Modifier
                .let {
                    if (state.bank.url != null)
                        it.clickable { bankDetailsExpanded = !bankDetailsExpanded }
                    else it
                },
            headlineContent = {
                Text(
                    text = state.bank.name,
                    maxLines = if (bankDetailsExpanded) 2 else 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            overlineContent = { Text("Банк") },
            trailingContent = {
                state.bank.let { bank ->
                    if (bank.url != null || bank.city != null || bank.phone != null) Box(Modifier) {
                        val rotationAngle by animateFloatAsState(
                            targetValue = if (bankDetailsExpanded) 180F else 0F,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutLinearInEasing
                            )

                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            null,
                            Modifier.rotate(rotationAngle)
                        )
                    }
                }
            }
        )
        AnimatedVisibility(
            bankDetailsExpanded,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Column {
                state.bank.let { bank ->
                    bank.city?.let { city ->
                        ListItem(
                            modifier = Modifier.combinedClickable(
                                onClick = { onUriOpen("geo:0,0?q=$city") },
                                onLongClick = { onCopy(city) }
                            ),
                            headlineContent = { Text(city) },
                            leadingContent = {
                                Icon(Icons.Outlined.Business, null)
                            }
                        )
                    }
                    bank.phone?.let { phone ->
                        ListItem(
                            modifier = Modifier.combinedClickable(
                                onClick = { onUriOpen("tel:$phone") },
                                onLongClick = {
                                    onCopy(phone)
                                }
                            ),
                            headlineContent = { Text(phone) },
                            leadingContent = {
                                Icon(Icons.Outlined.Phone, null)
                            }
                        )
                    }
                    bank.url?.let { url ->
                        ListItem(
                            modifier = Modifier.combinedClickable(
                                onClick = { onUriOpen(appendProtocolIfNeeded(url)) },
                                onLongClick = { onCopy(url) }
                            ),
                            headlineContent = { Text(url) },
                            leadingContent = {
                                Icon(Icons.AutoMirrored.Outlined.Launch, null)
                            }
                        )
                    }
                }
            }
        }
    }


    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = { onCopy(state.brand) }
        ),
        headlineContent = { Text(state.brand) },
        overlineContent = { Text("Платежная система") }
    )
    ListItem(
        headlineContent = {
            state.prepaid?.let { prepaid ->
                Text(buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = if (prepaid) Color.Unspecified
                            else disabledTextColor()
                        )
                    ) {
                        append("Да")
                    }
                    append(" / ")
                    withStyle(
                        style = SpanStyle(
                            color = if (!prepaid) Color.Unspecified
                            else disabledTextColor()
                        )
                    ) {
                        append("Нет")
                    }
                })
            } ?: Text("Неизвестно", color = disabledTextColor())
        },
        overlineContent = { Text("Предоплаченная") }
    )
    ListItem(
        headlineContent = {
            Row {
                Row(Modifier.weight(1f)) {
                    Text("Длина - ")
                    state.number.length?.let { length ->
                        Text(length.toString())
                    } ?: Text("неизвестно", color = disabledTextColor())

                }
                Row(Modifier.weight(1f)) {
                    Text("Lugn - ")
                    state.number.luhn?.let { luhn ->
                        Text(if (luhn) "есть" else "нет")
                    } ?: Text("неизвестно", color = disabledTextColor())

                }
            }
        },
        overlineContent = { Text("Номер карты") }
    )
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = { onUriOpen("geo:0,0?q=${state.country.name}") },
            onLongClick = { onCopy(state.country.name) }
        ),
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

private fun appendProtocolIfNeeded(url: String): String {
    return if (!url.startsWith("http://") || !url.startsWith("https://"))
        "http://$url" else url
}