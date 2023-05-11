package ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeedButton(enabled: Boolean = true, onClick : ()-> Unit, modifier: Modifier = Modifier, isSelected : Boolean = false, content: @Composable RowScope.() -> Unit){
    androidx.compose.material.Button(
        colors = if(isSelected) ButtonDefaults.buttonColors(backgroundColor = Color(189,189,189)) else ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
        onClick = onClick,
        content = content,
        shape = RoundedCornerShape(24.dp),
        enabled = enabled,
        modifier = modifier
    )
}