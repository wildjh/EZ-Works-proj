/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'upwork-green': '#14a800',
        'upwork-green-hover': '#118a00',
        'upwork-dark': '#001e00'
      }
    },
  },
  plugins: [],
}
