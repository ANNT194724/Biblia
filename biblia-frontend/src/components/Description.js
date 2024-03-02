import React, { useState } from 'react';
import { CCardText, CLink } from '@coreui/react';

const Description = ({ description }) => {
  const [expanded, setExpanded] = useState(false);

  const showSeeMore = description.length > MAX_DESCRIPTION_LENGTH;

  const handleSeeMore = () => {
    setExpanded(true);
  };

  return (
    <div>
      <CCardText>
        {expanded ? (
          <div dangerouslySetInnerHTML={{ __html: description }} />
        ) : (
          <div
            dangerouslySetInnerHTML={{
              __html: `${description.substring(0, MAX_DESCRIPTION_LENGTH)}...`,
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

const MAX_DESCRIPTION_LENGTH = 600; // Maximum characters to display initially

export default Description;
