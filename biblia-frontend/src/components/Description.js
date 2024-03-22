import React, { useState } from 'react';
import { CCardText, CLink } from '@coreui/react';

const Description = ({ description }) => {
  const [expanded, setExpanded] = useState(false);

  const showSeeMore = description.length > MAX_DESCRIPTION_LENGTH;

  const handleSeeMore = () => {
    setExpanded(true);
  };

  let substring = description.substring(0, MAX_DESCRIPTION_LENGTH);
  if (showSeeMore) substring += '...';

  return (
    <div>
      <CCardText className="mb-3">
        {expanded ? (
          <div dangerouslySetInnerHTML={{ __html: description }} />
        ) : (
          <div
            dangerouslySetInnerHTML={{
              __html: substring,
            }}
          />
        )}
      </CCardText>
      {showSeeMore && !expanded && (
        <CLink color="link" onClick={handleSeeMore}>
          See more
        </CLink>
      )}
    </div>
  );
};

const MAX_DESCRIPTION_LENGTH = 512;

export default Description;
