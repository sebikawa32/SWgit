import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";

export default function HotTicketSlider({ tickets }) {
  const [active, setActive] = useState(0);
  const [hovered, setHovered] = useState(null); // 🟢 hover 상태

  useEffect(() => {
    if (!tickets || tickets.length === 0) return;
    setActive(0);
    const timer = setInterval(() => {
      setActive((prev) => (prev + 1) % tickets.length);
    }, 4000);
    return () => clearInterval(timer);
  }, [tickets]);

  if (!tickets || tickets.length === 0) {
    return <p>인기 콘서트 정보 준비 중입니다.</p>;
  }

  // ⭐️ activeIdx = hovered !== null ? hovered : active
  const activeIdx = hovered !== null ? hovered : active;

  return (
    <div
      style={{
        width: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        overflowX: "visible",
        padding: "56px 0 32px 0",
        height: 320, // ★ 가장 큰 카드 높이로 고정 (active card height)
      }}
    >
      <div
        style={{
          display: "flex",
          gap: 66,
          alignItems: "flex-end",
          justifyContent: "center",
        }}
      >
        {tickets.map((ticket, idx) => (
          <motion.div
            key={ticket.id}
            style={{
              width: activeIdx === idx ? 260 : 221,
              height: activeIdx === idx ? 351 : 273,
              position: "relative",
              border: activeIdx === idx ? "2.5px solid #fff" : "none",
              boxShadow: activeIdx === idx ? "0 6px 20px 0rgba(32, 31, 30, 0.2)" : "0 1px 3px 0 #aaa2",
              cursor: "pointer",
              transition: "width 0.25s, height 0.25s",
              background: "#fff",
              overflow: "hidden",
              borderRadius: 0,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              transformOrigin: "bottom", 
            }}
            animate={{
              scale: activeIdx === idx ? 1.12 : 1,
              filter: activeIdx === idx ? "brightness(1.08)" : "brightness(0.93)",
              zIndex: activeIdx === idx ? 2 : 1,
            }}
            transition={{ type: "tween", duration: 0.4, ease: "easeInOut" }}
            onMouseEnter={() => setHovered(idx)}
            onMouseLeave={() => setHovered(null)}
          >
            <Link to={`/ticket/${ticket.id}`}>
              <img
                src={ticket.imageUrl}
                alt={ticket.title}
                style={{
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  borderRadius: 0,
                  display: "block",
                }}
              />
              <div
                style={{
                  position: "absolute",
                  top: 10,
                  left: 12,
                  background: "#ff7f00",
                  color: "#fff",
                  padding: "2px 10px",
                  borderRadius: 20,
                  fontWeight: 700,
                  fontSize: 15,
                  boxShadow: "0 2px 8px 0 rgba(5, 7, 46, 0.15)",
                  zIndex: 3,
                  opacity: activeIdx === idx ? 1 : 0.7
                }}
              >{`${idx + 1}위`}</div>
              <div
                style={{
                  position: "absolute",
                  bottom: 8,
                  left: 0,
                  width: "100%",
                  background: "rgba(5, 5, 5, 0.55)",
                  color: "#fff",
                  textAlign: "center",
                  fontSize: 15,
                  fontWeight: 600,
                  padding: "4px 0",
                  letterSpacing: 1,
                  zIndex: 2
                }}
              >
                {ticket.title}
              </div>
            </Link>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
