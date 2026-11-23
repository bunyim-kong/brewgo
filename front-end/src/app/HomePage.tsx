import { Link } from 'react-router-dom'
import cafeHero from '../assets/cafe-hero.webp'
import cafeHeroSmall from '../assets/cafe-hero-small.webp'

export default function HomePage() {
  return (
    <section className="container-shell hero">
      <div className="hero-copy">
        <p className="eyebrow">BrewGo Coffee Bar</p>
        <h1>Take a coffee break.</h1>
        <p className="hero-description">
          Espresso, silky matcha, and something from the bakery.
        </p>
        <Link className="button button-primary" to="/menu">
          Browse the menu <span aria-hidden="true">↗</span>
        </Link>
      </div>
      <img
        className="hero-image"
        src={cafeHero}
        srcSet={`${cafeHeroSmall} 640w, ${cafeHero} 1200w`}
        sizes="(min-width: 1100px) 600px, (min-width: 768px) 55vw, 100vw"
        alt="Cappuccino with latte art on the café counter"
        width={1200}
        height={800}
        fetchPriority="high"
      />
    </section>
  )
}
