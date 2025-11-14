/**
 * 购物车管理工具
 */

// 购物车存储key
const CART_KEY = 'cart'

// 获取购物车数据
export const getCart = () => {
	try {
		const cart = uni.getStorageSync(CART_KEY)
		return Array.isArray(cart) ? cart : []
	} catch (error) {
		console.error('获取购物车数据失败:', error)
		return []
	}
}

// 保存购物车数据
export const saveCart = (cart) => {
	try {
		uni.setStorageSync(CART_KEY, cart)
		// 触发购物车更新事件
		uni.$emit('cartUpdate', cart)
		return true
	} catch (error) {
		console.error('保存购物车数据失败:', error)
		return false
	}
}

// 添加商品到购物车
export const addToCart = (product, options = {}) => {
	const cart = getCart()
	
	// 构建购物车商品对象
	const cartItem = {
		id: product.id,
		name: product.name,
		image: product.image,
		price: product.price,
		memberPrice: product.memberPrice || product.price,
		description: product.description,
		categoryId: product.categoryId,
		// 定制选项
		sweetness: options.sweetness || 4, // 默认正常糖
		temperature: options.temperature || 2, // 默认正常冰
		toppings: options.toppings || [], // 加料
		remark: options.remark || '', // 备注
		// 购物车属性
		quantity: options.quantity || 1,
		selected: true, // 默认选中
		addTime: Date.now() // 添加时间
	}
	
	// 生成唯一标识（商品ID + 定制选项）
	const itemKey = generateCartItemKey(cartItem)
	cartItem.cartId = itemKey
	
	// 查找是否已存在相同配置的商品
	const existIndex = cart.findIndex(item => item.cartId === itemKey)
	
	if (existIndex > -1) {
		// 已存在，增加数量
		cart[existIndex].quantity += cartItem.quantity
		cart[existIndex].addTime = Date.now() // 更新添加时间
	} else {
		// 不存在，添加新商品
		cart.push(cartItem)
	}
	
	saveCart(cart)
	
	// 显示添加成功提示
	uni.showToast({
		title: '已加入购物车',
		icon: 'success',
		duration: 1500
	})
	
	return cart
}

// 生成购物车商品唯一标识
const generateCartItemKey = (item) => {
	const toppingsStr = Array.isArray(item.toppings) ? item.toppings.sort().join(',') : ''
	return `${item.id}_${item.sweetness}_${item.temperature}_${toppingsStr}`
}

// 更新购物车商品数量
export const updateCartItemQuantity = (cartId, quantity) => {
	const cart = getCart()
	const index = cart.findIndex(item => item.cartId === cartId)
	
	if (index > -1) {
		if (quantity <= 0) {
			// 数量为0，删除商品
			cart.splice(index, 1)
		} else {
			cart[index].quantity = quantity
		}
		saveCart(cart)
	}
	
	return cart
}

// 删除购物车商品
export const removeFromCart = (cartId) => {
	const cart = getCart()
	const index = cart.findIndex(item => item.cartId === cartId)
	
	if (index > -1) {
		cart.splice(index, 1)
		saveCart(cart)
	}
	
	return cart
}

// 清空购物车
export const clearCart = () => {
	saveCart([])
	uni.showToast({
		title: '购物车已清空',
		icon: 'success'
	})
	return []
}

// 切换商品选中状态
export const toggleCartItemSelected = (cartId) => {
	const cart = getCart()
	const index = cart.findIndex(item => item.cartId === cartId)
	
	if (index > -1) {
		cart[index].selected = !cart[index].selected
		saveCart(cart)
	}
	
	return cart
}

// 全选/取消全选
export const toggleAllSelected = (selected) => {
	const cart = getCart()
	cart.forEach(item => {
		item.selected = selected
	})
	saveCart(cart)
	return cart
}

// 获取购物车商品总数量
export const getCartTotalCount = () => {
	const cart = getCart()
	return cart.reduce((total, item) => total + item.quantity, 0)
}

// 获取选中商品总数量
export const getSelectedTotalCount = () => {
	const cart = getCart()
	return cart
		.filter(item => item.selected)
		.reduce((total, item) => total + item.quantity, 0)
}

// 获取选中商品总价格
export const getSelectedTotalPrice = (isMember = false) => {
	const cart = getCart()
	return cart
		.filter(item => item.selected)
		.reduce((total, item) => {
			const price = isMember && item.memberPrice ? item.memberPrice : item.price
			return total + (price * item.quantity)
		}, 0)
}

// 获取选中的商品列表
export const getSelectedItems = () => {
	const cart = getCart()
	return cart.filter(item => item.selected)
}

// 检查是否全选
export const isAllSelected = () => {
	const cart = getCart()
	if (cart.length === 0) return false
	return cart.every(item => item.selected)
}

// 检查购物车是否为空
export const isCartEmpty = () => {
	const cart = getCart()
	return cart.length === 0
}

// 获取购物车统计信息
export const getCartStats = () => {
	const cart = getCart()
	const selectedItems = cart.filter(item => item.selected)
	
	return {
		totalCount: cart.reduce((total, item) => total + item.quantity, 0),
		selectedCount: selectedItems.reduce((total, item) => total + item.quantity, 0),
		totalPrice: cart.reduce((total, item) => total + (item.price * item.quantity), 0),
		selectedPrice: selectedItems.reduce((total, item) => total + (item.price * item.quantity), 0),
		selectedMemberPrice: selectedItems.reduce((total, item) => {
			const price = item.memberPrice || item.price
			return total + (price * item.quantity)
		}, 0),
		itemCount: cart.length,
		selectedItemCount: selectedItems.length
	}
}

export default {
	getCart,
	saveCart,
	addToCart,
	updateCartItemQuantity,
	removeFromCart,
	clearCart,
	toggleCartItemSelected,
	toggleAllSelected,
	getCartTotalCount,
	getSelectedTotalCount,
	getSelectedTotalPrice,
	getSelectedItems,
	isAllSelected,
	isCartEmpty,
	getCartStats
}
