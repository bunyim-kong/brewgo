export type Product = {
  id: number
  slug: string
  name: string
  description: string | null
  allergens: string | null
  basePrice: string
  currency: string
}

export function formatPrice(product: Product) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: product.currency,
  }).format(Number(product.basePrice))
}
