/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'sans-serif'],
        display: ['Sora', 'sans-serif']
      },
      colors: {
        brand: {
          ink: '#0f172a',
          night: '#111827',
          mist: '#e2e8f0',
          sand: '#f8fafc',
          teal: '#0f766e',
          amber: '#d97706',
          coral: '#dc2626'
        }
      },
      boxShadow: {
        soft: '0 24px 60px -24px rgba(15, 23, 42, 0.24)',
        glass: '0 14px 30px -18px rgba(15, 23, 42, 0.22)'
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-6px)' }
        },
        'pulse-soft': {
          '0%, 100%': { opacity: '0.85' },
          '50%': { opacity: '1' }
        }
      },
      animation: {
        float: 'float 5s ease-in-out infinite',
        'pulse-soft': 'pulse-soft 2.2s ease-in-out infinite'
      }
    }
  },
  plugins: []
};
