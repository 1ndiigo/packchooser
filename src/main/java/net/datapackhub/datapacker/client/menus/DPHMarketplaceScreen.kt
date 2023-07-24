package net.datapackhub.datapacker.client.menus

import io.wispforest.owo.ui.base.BaseUIModelScreen
import io.wispforest.owo.ui.container.FlowLayout
import net.minecraft.util.Identifier

class DPHMarketplaceScreen : BaseUIModelScreen<FlowLayout>(
    FlowLayout::class.java,
    DataSource.asset(Identifier("datapacker", "dph_marketplace"))
) {
    override fun build(rootComponent: FlowLayout) {

    }
}
