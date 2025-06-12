// MapEmbed.jsx
import React from 'react';

const MapEmbed = ({ venue }) => {
  if (!venue) return null;
  const encodedVenue = encodeURIComponent(venue);

  return (
    <div style={{ marginTop: "16px", width: "100%", height: "300px" }}>
      <h4>📍 지도 보기</h4>
      <iframe
        title="map"
        width="100%"
        height="100%"
        frameBorder="0"
        style={{ border: 0, borderRadius: "8px" }}
        src={`https://www.google.com/maps?q=${encodedVenue}&output=embed`}
        allowFullScreen
      ></iframe>
    </div>
  );
};

export default MapEmbed;
