import { useEffect, useState } from 'react';
import { motion, useSpring, useTransform } from 'framer-motion';

export default function AnimatedCounter({ value, prefix = "", duration = 1.5 }) {
  const [hasMounted, setHasMounted] = useState(false);
  
  useEffect(() => {
    setHasMounted(true);
  }, []);

  const spring = useSpring(0, {
    bounce: 0,
    duration: duration * 1000,
  });

  useEffect(() => {
    if (hasMounted) {
      // Parse string with dollar signs like "$12.50" to float
      let numVal = typeof value === 'string' ? parseFloat(value.replace(/[^0-9.-]+/g, "")) : value;
      spring.set(numVal || 0);
    }
  }, [value, hasMounted, spring]);

  const display = useTransform(spring, (current) => {
    const isFloat = typeof value === 'string' && value.includes('.');
    if (isFloat) {
      return `${prefix}${current.toFixed(2)}`;
    }
    return `${prefix}${Math.round(current)}`;
  });

  return <motion.span>{display}</motion.span>;
}
