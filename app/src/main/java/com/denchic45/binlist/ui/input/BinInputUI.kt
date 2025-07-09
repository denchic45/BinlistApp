package com.denchic45.binlist.ui.input

import android.content.ClipData
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.denchic45.binlist.domain.model.BinDetailsRequest
import com.denchic45.binlist.domain.model.PrepaidCard
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                title = { Text("BinList") },
                actions = {
                    IconButton(onClick = navigateToHistory) {
                        Icon(Icons.Outlined.History, null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        BoxWithConstraints {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(modifier = Modifier.height(this@BoxWithConstraints.maxHeight / 3)) {
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = {
                                if (it.length <= 8 && it.isDigitsOnly())
                                    viewModel.onInputChange(it)
                            },
                            modifier = Modifier.width(248.dp),
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
                                    "Например: 4571 7360",
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
                        Button(
                            onClick = viewModel::onFindClick,
                            enabled = isValid && uiState !is BinDetailsUiState.Loading
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AnimatedVisibility(uiState is BinDetailsUiState.Loading) {
                                    CircularProgressIndicator(
                                        Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("Найти")
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                AnimatedContent(
                    uiState,
                    transitionSpec = {
                        (slideInVertically { -it / 8 } + fadeIn())
                            .togetherWith(slideOutVertically { it / 8 } + fadeOut())
                    }
                ) { state ->
                    Column(Modifier.fillMaxSize()) {
                        when (state) {
                            BinDetailsUiState.None -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Введите первые 6-8 цифр номера карты (BIN/IIN)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            BinDetailsUiState.Loading -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Загрузка", style = MaterialTheme.typography.bodyMedium)
                            }

                            is BinDetailsUiState.Success -> BinDetailsContent(
                                state.bindDetails,
                                onCopy = { text ->
                                    coroutine.launch {
                                        clipboard.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText(
                                                    "text",
                                                    text
                                                )
                                            )
                                        )
                                    }
                                    Toast.makeText(
                                        context,
                                        "Скопировано в буфер обмена",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                },
                                onUriOpen = { uri ->
                                    launcher.launch(
                                        Intent(Intent.ACTION_VIEW, uri.toUri())
                                    )
                                }
                            )

                            BinDetailsUiState.TooManyRequests -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Превышен лимит запросов. Попробуйте позже",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            BinDetailsUiState.InputError -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Убедитесь в правильности ввода",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            BinDetailsUiState.NoConnection -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Ошибка соединения",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            BinDetailsUiState.NotFound -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Ничего не найдено",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            BinDetailsUiState.ServerError -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Ошибка на уровне сервера",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            BinDetailsUiState.UnknownError -> Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Неизвестная ошибка",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun BinDetailsContent(
    state: BinDetailsRequest,
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

    state.bank?.let { bank ->
        Column(
            Modifier
                .graphicsLayer { this.shadowElevation = elevation.value.toPx() }
                .zIndex(if (bankDetailsExpanded) 1f else 0f)) {
            ListItem(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        if (bank.details != null)
                            bankDetailsExpanded = !bankDetailsExpanded
                    },
                    onLongClick = { onCopy(bank.name) }
                ),
                headlineContent = {
                    Text(
                        text = bank.name,
                        maxLines = if (bankDetailsExpanded) 2 else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                overlineContent = { Text("Банк") },
                trailingContent = {
                    bank.let { bank ->
                        if (bank.details != null) Box(Modifier) {
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
                    bank.details?.let { details ->
                        details.city.let { city ->
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
                        details.phone.let { phone ->
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
                        details.url.let { url ->
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
    }


    state.brand?.let { brand ->
        ListItem(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = { onCopy(brand) }
            ),
            headlineContent = { Text(brand) },
            overlineContent = { Text("Платежная система") }
        )
    }

    ListItem(
        headlineContent = {
            when (state.prepaid) {
                PrepaidCard.YES -> Text(buildAnnotatedString {
                    append("Да / ")
                    withStyle(SpanStyle(color = disabledTextColor())) {
                        append("Нет")
                    }
                })

                PrepaidCard.NO -> Text(buildAnnotatedString {
                    withStyle(SpanStyle(color = disabledTextColor())) {
                        append("Да")
                    }
                    append(" / Нет")
                })

                PrepaidCard.UNKNOWN -> {
                    Text("Неизвестно", color = disabledTextColor())
                }
            }
        },
        overlineContent = { Text("Предоплаченная") }
    )
    ListItem(
        headlineContent = {
            Row {
                state.number?.let { number ->
                    Row(Modifier.weight(1f)) {
                        Text("Длина - ")
                        Text(state.number.length.toString())
                    }
                    Row(Modifier.weight(1f)) {
                        Text("Lugn - ")
                        Text(if (state.number.luhn) "есть" else "нет")
                    }
                } ?: Text("Неизвестно", color = disabledTextColor())
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